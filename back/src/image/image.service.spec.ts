import { Test, TestingModule } from '@nestjs/testing';
import typeorm = require('typeorm');
import sizeOf from 'image-size';
import {
  getHostImage,
  getImagePermissionsDetails,
  getPermissionData,
} from './helpers';
import { ImageService } from './image.service';
import { setTypeormRepoMocked } from '../mocks/typeorm';
import { userMocked } from '../mocks/user';
import { fileMocked, imageMocked, imagesMocked } from '../mocks/image';
import { HttpException, HttpStatus } from '@nestjs/common';

jest.mock('image-size');
jest.mock('./helpers');
describe('ImageService', () => {
  let service: ImageService;
  beforeAll(() => {
    typeorm.getConnection = setTypeormRepoMocked();
    (sizeOf as jest.Mock).mockImplementation(() => ({ width: 0, height: 0 }));
  });

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [ImageService],
    }).compile();

    service = module.get<ImageService>(ImageService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('create', () => {
    beforeAll(() => {
      typeorm.getRepository = setTypeormRepoMocked(null, userMocked);
    });

    it('should create image', async () => {
      expect(await service.create(userMocked.id, fileMocked)).toEqual(
        `${fileMocked.filename} has been uploaded`,
      );
    });

    it('should not create image, bad request', async () => {
      typeorm.getConnection = jest.fn().mockReturnValue({});
      try {
        await service.create(userMocked.id, fileMocked);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).toHaveBeenCalled();
    });

    it('should not create image because user not found', async () => {
      typeorm.getConnection = setTypeormRepoMocked();
      typeorm.getRepository = setTypeormRepoMocked();
      try {
        await service.create(userMocked.id, fileMocked);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).not.toHaveBeenCalled();
    });

    it('should not create image because filename not found', async () => {
      typeorm.getRepository = setTypeormRepoMocked();
      try {
        await service.create(userMocked.id, undefined);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getRepository).not.toHaveBeenCalled();
    });
  });

  describe('remove', () => {
    beforeAll(() => {
      typeorm.getConnection = setTypeormRepoMocked();
      typeorm.getRepository = setTypeormRepoMocked(null, imageMocked);
    });
    it('should remove image', async () => {
      expect(await service.remove(0)).toBe(`Image 0 has been deleted`);
    });

    it('should not remove image, bad request', async () => {
      typeorm.getConnection = jest.fn().mockReturnValue({});
      try {
        await service.remove(0);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).toHaveBeenCalled();
    });

    it('should not remove image because image not found', async () => {
      typeorm.getConnection = setTypeormRepoMocked();
      typeorm.getRepository = setTypeormRepoMocked();
      try {
        await service.remove(1);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).not.toHaveBeenCalled();
    });
  });
  describe('findByUserId', () => {
    it('should return an array of users', async () => {
      typeorm.getRepository = setTypeormRepoMocked(imagesMocked);
      expect(await service.findByUserId(0, 0)).toEqual(imagesMocked);
    });
  });

  describe('add permission', () => {
    beforeAll(() => {
      (getPermissionData as jest.Mock).mockImplementation(() => ({
        user: userMocked,
        image: imageMocked,
      }));
      (getHostImage as jest.Mock).mockImplementation(() => imageMocked);
    });

    it('should add permission', async () => {
      expect(
        await service.addPermission(
          {
            userId: userMocked.id,
            imageId: imageMocked.id,
          },
          0,
        ),
      ).toEqual(
        `image "${imageMocked.name}" is readable for user : ${userMocked.login}`,
      );
    });

    it('should not add image permission, bad request', async () => {
      typeorm.getConnection = jest.fn().mockReturnValue({});
      try {
        await service.addPermission(
          {
            userId: userMocked.id,
            imageId: imageMocked.id,
          },
          0,
        );
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).toHaveBeenCalled();
    });

    it('should not add image permission, permission already exists', async () => {
      typeorm.getRepository = setTypeormRepoMocked(null, imageMocked);
      jest.clearAllMocks();
      try {
        await service.addPermission(
          {
            userId: userMocked.id,
            imageId: imageMocked.id,
          },
          0,
        );
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(getPermissionData).not.toHaveBeenCalled();
    });

    it('should not add image permission because user not found', async () => {
      typeorm.getRepository = setTypeormRepoMocked();
      jest.clearAllMocks();
      (getPermissionData as jest.Mock).mockImplementation(() => {
        throw new HttpException(
          'Image or user not found',
          HttpStatus.NOT_FOUND,
        );
      });
      typeorm.getConnection = setTypeormRepoMocked();

      try {
        await service.addPermission(
          {
            userId: userMocked.id,
            imageId: imageMocked.id,
          },
          0,
        );
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(getPermissionData).toHaveBeenCalled();
      expect(typeorm.getConnection).not.toHaveBeenCalled();
    });

    it('should not add image permission, becaud hostID and userId are the same', async () => {
      jest.clearAllMocks();
      try {
        await service.addPermission(
          {
            userId: 0,
            imageId: imageMocked.id,
          },
          0,
        );
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(getHostImage).not.toHaveBeenCalled();
    });
  });

  describe('delete permission', () => {
    beforeAll(() => {
      (getPermissionData as jest.Mock).mockImplementation(() => ({
        user: userMocked,
        image: imageMocked,
      }));
      (getHostImage as jest.Mock).mockImplementation(() => imageMocked);
    });

    it('should delete permission', async () => {
      expect(
        await service.removePermission(
          {
            userId: userMocked.id,
            imageId: imageMocked.id,
          },
          0,
        ),
      ).toEqual(
        `image "${imageMocked.name}" is no more readable for user : ${userMocked.login}`,
      );
    });

    it('should not remove image permission, bad request', async () => {
      typeorm.getConnection = jest.fn().mockReturnValue({});
      try {
        await service.removePermission(
          {
            userId: userMocked.id,
            imageId: imageMocked.id,
          },
          0,
        );
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).toHaveBeenCalled();
    });

    it('should not remove image permission because user not found', async () => {
      (getPermissionData as jest.Mock).mockImplementation(() => {
        throw new HttpException(
          'Image or user not found',
          HttpStatus.NOT_FOUND,
        );
      });
      typeorm.getConnection = setTypeormRepoMocked();
      try {
        await service.removePermission(
          {
            userId: userMocked.id,
            imageId: imageMocked.id,
          },
          0,
        );
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).not.toHaveBeenCalled();
    });

    it('should not remove image permission, becaud hostID and userId are the same', async () => {
      jest.clearAllMocks();
      try {
        await service.removePermission(
          {
            userId: 0,
            imageId: imageMocked.id,
          },
          0,
        );
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(getHostImage).not.toHaveBeenCalled();
    });
  });

  describe('findOne', () => {
    beforeAll(() => {
      (getImagePermissionsDetails as jest.Mock).mockImplementation(() => []);
      typeorm.getRepository = setTypeormRepoMocked(null, imageMocked);
    });
    it('should return one of image', async () => {
      expect(await service.findOne(1)).toEqual(imageMocked);
    });

    it('should not return one of image', async () => {
      typeorm.getRepository = setTypeormRepoMocked();
      try {
        await service.findOne(1);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
    });
  });
});
