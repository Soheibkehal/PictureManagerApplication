import * as fs from 'fs';
import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { User } from '../user/entities/user.entity';

import { getConnection, getRepository } from 'typeorm';
import { Image } from './entities/image.entity';
import {
  getHostImage,
  getImagePermissionsDetails,
  getMetadata,
  getPermissionData,
} from './helpers';
import { ImagePermissions } from '../permissions/entities/image-permissions.entity';

@Injectable()
export class ImageService {
  async create(userId: number, file: Express.Multer.File) {
    console.log(file);
    if (!file)
      throw new HttpException(
        { message: 'Image not provided' },
        HttpStatus.BAD_REQUEST,
      );

    const user = await getRepository(User)
      .createQueryBuilder('user')
      .where('user.id = :id', { id: userId })
      .getOne();

    if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND);

    const values = {
      user,
      name: file.filename,
      metadata: getMetadata(file),
    };

    try {
      await getConnection()
        .createQueryBuilder()
        .insert()
        .into(Image)
        .values(values)
        .execute();
    } catch (error) {
      throw new HttpException(
        'Image has not been created',
        HttpStatus.BAD_REQUEST,
      );
    }
    return { message: `${file.filename} has been uploaded` };
  }

  async remove(userId: number, id: number) {
    const image = await getRepository(Image)
      .createQueryBuilder('image')
      .where('image.id = :id', { id })
      .andWhere('image.userId = :userId', { userId })
      .getOne();

    if (!image)
      throw new HttpException('Image not found', HttpStatus.NOT_FOUND);

    try {
      fs.unlinkSync(`./uploads/${image.name}`);
    } catch (err) {
      console.log(err);
    }

    try {
      await getConnection()
        .createQueryBuilder()
        .delete()
        .from(Image)
        .where('id = :id', { id })
        .execute();
    } catch (error) {
      throw new HttpException(
        'Image has not been deleted',
        HttpStatus.BAD_REQUEST,
      );
    }

    return `Image ${id} has been deleted`;
  }

  async findOne(id: number) {
    console.log(id);
    const image = await getRepository(Image)
      .createQueryBuilder('image')
      .leftJoinAndSelect('image.imagePermissions', 'imagePermissions')
      .leftJoinAndSelect('image.user', 'user')
      .where('image.id = :id', { id })
      .getOne();

    if (!image)
      throw new HttpException(
        { message: 'Image not found' },
        HttpStatus.NOT_FOUND,
      );

    image.imagePermissions = await getImagePermissionsDetails(id);

    return image;
  }

  //TODO
  async findByUserId(userId: number, hostId: number) {
    if (hostId === userId) {
      const images = await getRepository(Image)
        .createQueryBuilder('image')
        .andWhere('image.userId = :userId', { userId })
        .getMany();

      return images;
    }

    const imagePermissions = await getRepository(ImagePermissions)
      .createQueryBuilder('imagePermissions')
      .leftJoinAndSelect('imagePermissions.image', 'image')
      .where('imagePermissions.userId = :hostId', { hostId })
      .andWhere('image.userId = :userId', { userId })
      .getMany();

    return imagePermissions;
  }

  async addPermission(
    {
      userId,
      imageId,
    }: {
      userId: number;
      imageId: number;
    },
    hostId: number,
  ) {
    if (hostId == userId)
      throw new HttpException(
        'Can not add permission for itself',
        HttpStatus.BAD_REQUEST,
      );

    await getHostImage(imageId, hostId);

    const permission = await getRepository(ImagePermissions)
      .createQueryBuilder('imagePermissions')
      .where('imagePermissions.imageId = :imageId', { imageId })
      .andWhere('imagePermissions.userId = :userId', { userId })
      .getOne();

    if (permission)
      throw new HttpException(
        'Permissions already added',
        HttpStatus.FORBIDDEN,
      );
    const { image, user } = await getPermissionData(imageId, userId);

    const values = {
      user,
      image,
    };

    try {
      await getConnection()
        .createQueryBuilder()
        .insert()
        .into(ImagePermissions)
        .values(values)
        .execute();
    } catch (error) {
      throw new HttpException(
        'Image permissions has not been added',
        HttpStatus.BAD_REQUEST,
      );
    }
    return {
      message: `image "${image.name}" is readable for user : ${user.login}`,
    };
  }

  async removePermission(
    {
      userId,
      imageId,
    }: {
      userId: number;
      imageId: number;
    },
    hostId: number,
  ) {
    if (hostId == userId)
      throw new HttpException(
        'Can not add permission for itself',
        HttpStatus.BAD_REQUEST,
      );

    await getHostImage(imageId, hostId);

    const { image, user } = await getPermissionData(imageId, userId);
    try {
      await getConnection()
        .createQueryBuilder()
        .delete()
        .from(ImagePermissions)
        .where('imageId = :imageId', { imageId })
        .andWhere('userId = :userId', { userId })
        .execute();
    } catch (error) {
      throw new HttpException(
        'Image permissions has not been deleted',
        HttpStatus.BAD_REQUEST,
      );
    }

    return {
      message: `image "${image.name}" is no more readable for user : ${user.login}`,
    };
  }
}
