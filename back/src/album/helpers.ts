import { HttpException, HttpStatus } from '@nestjs/common';
import { getRepository } from 'typeorm';

import { User } from '../user/entities/user.entity';
import { Album } from './entities/album.entity';
import { AlbumPermissions } from 'src/permissions/entities/album-permissions.entity';
import { Image } from 'src/image/entities/image.entity';



export const getAlbumImagesData = async (albumId: number, imageId: number) => {
  const album = await getRepository(Album)
    .createQueryBuilder('album')
    .where('album.id = :id', { id: albumId })
    .leftJoinAndSelect('album.user', 'user')
    .getOne();

  const image = await getRepository(Image)
    .createQueryBuilder('image')
    .where('image.id = :id', { id: imageId })
    .leftJoinAndSelect('image.user', 'user')
    .getOne();

  if (!image || !album)
    throw new HttpException('Image or user not found', HttpStatus.NOT_FOUND);

  return { image, album };
};

export const getAlbumPermissionData = async (albumId: number, userId: number) => {
  const album = await getRepository(Album)
    .createQueryBuilder('album')
    .where('album.id = :id', { id: albumId })
    .getOne();

  const user = await getRepository(User)
    .createQueryBuilder('user')
    .where('user.id = :id', { id: userId })
    .getOne();

  if (!user || !album)
    throw new HttpException('Album or user not found', HttpStatus.NOT_FOUND);

  return { user, album };
};

export const getAlbumPermissionsDetails = async (id: number) => {
  const albumPermissions = await getRepository(AlbumPermissions)
    .createQueryBuilder('albumPermissions')
    .innerJoin('albumPermissions.album', 'album')
    .leftJoinAndSelect('albumPermissions.user', 'user')
    .where('album.id = :id', { id })
    .getMany();

  return albumPermissions;
};

export const getHostAlbum = async (albumId: number, hostId: number) => {
  const hostAlbum = await getRepository(Album)
    .createQueryBuilder('album')
    .where('album.id = :albumId', { albumId })
    .andWhere('album.userId = :hostId', { hostId })
    .getOne();

  if (!hostAlbum) {
    throw new HttpException('Your album is not found', HttpStatus.NOT_FOUND);
  }
  return hostAlbum;
};
