import { Test, TestingModule } from '@nestjs/testing';
import { HttpException } from '@nestjs/common';
import typeorm = require('typeorm');

import { UserService } from './user.service';
import { usersMocked, userMocked, listUsersMocked } from '../mocks/user';
import { setTypeormRepoMocked } from '../mocks/typeorm';
import { imageMocked } from '../mocks/image';
import { permissionsMocked } from '../mocks/imagePermissions';

describe('UserService', () => {
  beforeAll(() => {
    typeorm.getRepository = setTypeormRepoMocked(usersMocked, userMocked);
    typeorm.getConnection = setTypeormRepoMocked();
  });

  let service: UserService;
  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [UserService],
    }).compile();

    service = module.get<UserService>(UserService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('create', () => {
    beforeAll(() => {
      typeorm.getRepository = setTypeormRepoMocked(usersMocked, null);
    });
    it('should create user', async () => {
      expect(await service.create(userMocked)).toEqual(
        `User test has been created`,
      );
    });

    it('should not create user, bad request', async () => {
      typeorm.getConnection = jest.fn().mockReturnValue({});
      try {
        await service.create(userMocked);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).toHaveBeenCalled();
    });

    it('should not create user because hes already exists', async () => {
      typeorm.getConnection = setTypeormRepoMocked();
      typeorm.getRepository = setTypeormRepoMocked(usersMocked, userMocked);
      try {
        await service.create(userMocked);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).not.toHaveBeenCalled();
    });
  });

  describe('findAll', () => {
    it('should return an array of users', async () => {
      typeorm.getRepository = setTypeormRepoMocked(listUsersMocked, userMocked);
      expect(await service.findAll()).toEqual(listUsersMocked);
    });
  });

  // describe('findOne', () => {
  //   it('should return one of users', async () => {
  //     typeorm.getRepository = setTypeormRepoMocked(usersMocked, userMocked);
  //     expect(await service.findOne(1)).toEqual(userMocked);
  //   });

  //   it('should not return one of users', async () => {
  //     typeorm.getRepository = setTypeormRepoMocked(usersMocked, null);
  //     try {
  //       await service.findOne(1);
  //     } catch (error) {
  //       expect(error).toBeInstanceOf(HttpException);
  //     }
  //   });
  // });

  describe('getFeed', () => {
    beforeAll(() => {
      typeorm.getRepository = setTypeormRepoMocked(
        permissionsMocked,
        userMocked,
      );
    });
    it('should get Feed', async () => {
      expect(await service.getFeed(0)).toEqual([imageMocked]);
    });

    it('should not get feed because user not found', async () => {
      typeorm.getRepository = setTypeormRepoMocked();
      try {
        await service.getFeed(0);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
    });
  });

  describe('update', () => {
    beforeAll(() => {
      typeorm.getConnection = setTypeormRepoMocked();
      typeorm.getRepository = setTypeormRepoMocked(usersMocked, userMocked);
    });
    it('should update user', async () => {
      expect(await service.update(1, userMocked)).toBe(
        'User test has been updated (id: 1)',
      );
    });

    it('should not update user, bad request', async () => {
      typeorm.getConnection = jest.fn().mockReturnValue({});
      try {
        await service.update(1, userMocked);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).toHaveBeenCalled();
    });

    it('should not update user because user not found', async () => {
      typeorm.getConnection = setTypeormRepoMocked();
      typeorm.getRepository = setTypeormRepoMocked(usersMocked, null);
      try {
        await service.update(1, userMocked);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).not.toHaveBeenCalled();
    });
  });

  describe('remove', () => {
    beforeAll(() => {
      typeorm.getConnection = setTypeormRepoMocked();
      typeorm.getRepository = setTypeormRepoMocked(usersMocked, userMocked);
    });
    it('should remove user', async () => {
      expect(await service.remove(1)).toBe('User 1 has been deleted');
    });

    it('should not remove user, bad request', async () => {
      typeorm.getConnection = jest.fn().mockReturnValue({});
      try {
        await service.remove(1);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).toHaveBeenCalled();
    });

    it('should not remove user because user not found', async () => {
      typeorm.getConnection = setTypeormRepoMocked();
      typeorm.getRepository = setTypeormRepoMocked(usersMocked, null);
      try {
        await service.remove(1);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
      expect(typeorm.getConnection).not.toHaveBeenCalled();
    });
  });
});
