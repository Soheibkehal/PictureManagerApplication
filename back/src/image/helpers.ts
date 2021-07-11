import { diskStorage } from 'multer';
import { extname } from 'path';
import { HttpException, HttpStatus } from '@nestjs/common';
import e from 'express';
import sizeOf from 'image-size';
import { getRepository } from 'typeorm';

import { User } from '../user/entities/user.entity';
import { Image } from './entities/image.entity';
import { ImagePermissions } from '../permissions/entities/image-permissions.entity';

const setFileFilter = (
  _req: e.Request,
  file: Express.Multer.File,
  callback: (error: Error, acceptFile: boolean) => void,
) => {
  if (!file.originalname.match(/\.(jpg|jpeg|png|gif|JPG)$/)) {
    return callback(
      new HttpException(
        { message: 'Only image files are allowed!' },
        HttpStatus.BAD_REQUEST,
      ),
      false,
    );
  }

  callback(null, true);
};

const setFilename = (
  _req: e.Request,
  file: Express.Multer.File,
  callback: (error: Error, filename: string) => void,
) => {
  const name = file.originalname.split('.')[0];
  const fileExtName = extname(file.originalname);
  const randomName = Array(4)
    .fill(null)
    .map(() => Math.round(Math.random() * 10).toString(10))
    .join('');
  callback(null, `${name}${randomName}${fileExtName}`);
};

export const imageUploadConfig = {
  storage: diskStorage({
    destination: './uploads',
    filename: setFilename,
  }),
  fileFilter: setFileFilter,
};

export const getMetadata = (file: Express.Multer.File) => {
  const dimensions = sizeOf(file.path);
  return JSON.stringify({ ...file, ...dimensions });
};

export const getPermissionData = async (imageId: number, userId: number) => {
  const image = await getRepository(Image)
    .createQueryBuilder('image')
    .where('image.id = :id', { id: imageId })
    .getOne();

  const user = await getRepository(User)
    .createQueryBuilder('user')
    .where('user.id = :id', { id: userId })
    .getOne();

  if (!user || !image)
    throw new HttpException('Image or user not found', HttpStatus.NOT_FOUND);

  return { user, image };
};

export const getImagePermissionsDetails = async (id: number) => {
  const imagePermissions = await getRepository(ImagePermissions)
    .createQueryBuilder('imagePermissions')
    .innerJoin('imagePermissions.image', 'image')
    .leftJoinAndSelect('imagePermissions.user', 'user')
    .where('image.id = :id', { id })
    .getMany();

  return imagePermissions;
};

export const getHostImage = async (imageId: number, hostId: number) => {
  const hostImage = await getRepository(Image)
    .createQueryBuilder('image')
    .where('image.id = :imageId', { imageId })
    .andWhere('image.userId = :hostId', { hostId })
    .getOne();

  if (!hostImage) {
    throw new HttpException('Your image is not found', HttpStatus.NOT_FOUND);
  }
  return hostImage;
};
