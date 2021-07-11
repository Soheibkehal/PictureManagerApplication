import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { User } from '../user/entities/user.entity';
import { getConnection, getRepository } from 'typeorm';
import { Album } from './entities/album.entity';
import { Image } from '../image/entities/image.entity';
import { UpdateAlbumDto } from './dto/update-album.dto';
import { AlbumPermissions } from 'src/permissions/entities/album-permissions.entity';
import {
  getAlbumImagesData,
  getAlbumPermissionData,
  getAlbumPermissionsDetails,
  getHostAlbum,
} from './helpers';

@Injectable()
export class AlbumService {
  async create(
    userId: number,
    albumName: string /*createAlbumDto: CreateAlbumDto*/,
  ) {
    if (!albumName)
      throw new HttpException(
        'Album name not provided',
        HttpStatus.BAD_REQUEST,
      );

    const user = await getRepository(User)
      .createQueryBuilder('user')
      .where('user.id = :id', { id: userId })
      .getOne();

    if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    const date = new Date();

    const values = {
      user,
      timestamp: date,
      name: albumName,
    };

    try {
      await getConnection()
        .createQueryBuilder()
        .insert()
        .into(Album)
        .values(values)
        .execute();
    } catch (error) {
      console.log(error);
      throw new HttpException(
        'An error occured while creating the album',
        HttpStatus.BAD_REQUEST,
      );
    }
    return `${albumName} album has been created`;
  }

  async findOne(hostId: number, id: number) {
    const album = await getRepository(Album)
      .createQueryBuilder('album')
      .leftJoinAndSelect('album.images', 'images')
      .leftJoinAndSelect('album.user', 'user')
      .where('album.id = :id', { id })
      .getOne();

    album.albumPermissions = await getAlbumPermissionsDetails(id);

    const isUserAuthorized = album.albumPermissions.find(
      ({ user }) => user.id == hostId,
    );

    if (album.user.id != hostId && !isUserAuthorized) {
      throw new HttpException(
        'You need to be authorized by the author',
        HttpStatus.UNAUTHORIZED,
      );
    }

    return album;
  }

  async update(userId: number, id: number, name: string) {
    if (!name)
      throw new HttpException(
        'Please provide a new name to the album',
        HttpStatus.BAD_REQUEST,
      );
    if (!id)
      throw new HttpException(
        "Please provide the album's id",
        HttpStatus.BAD_REQUEST,
      );
    if (!userId)
      throw new HttpException(
        'You need to be logged to do this action',
        HttpStatus.UNAUTHORIZED,
      );

    const album = await getRepository(Album)
      .createQueryBuilder('album')
      .where('album.id = :id', { id })
      .andWhere('album.userId = :userId', { userId })
      .getOne();

    if (!album)
      throw new HttpException('Album not found', HttpStatus.NOT_FOUND);

    try {
      await getConnection()
        .createQueryBuilder()
        .update(Album)
        .set({ name })
        .where('id = :id', { id })
        .execute();
      return `Album ${name} has been updated (id: ${id})`;
    } catch (error) {
      throw new HttpException(
        'An error occured while updating the album',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }

  async remove(userId, id: number) {
    const album = await getRepository(Album)
      .createQueryBuilder('album')
      .where('album.id = :id', { id })
      .andWhere('album.userId = :userId', { userId })
      .getOne();

    if (!album)
      throw new HttpException('Album not found', HttpStatus.NOT_FOUND);

    try {
      await getConnection()
        .createQueryBuilder()
        .delete()
        .from(Album)
        .where('id = :id', { id })
        .execute();
    } catch (error) {
      throw new HttpException(
        'Album has not been deleted',
        HttpStatus.BAD_REQUEST,
      );
    }

    return `Album ${id} has been deleted`;
  }

  async findByUserId(hostId: number, userId: number) {
    if (hostId === userId) {
      const albums = await getRepository(Album)
        .createQueryBuilder('album')
        .where('album.userId = :userId', { userId })
        .getMany();

      return albums;
    }

    const albumsPermissions = await getRepository(AlbumPermissions)
      .createQueryBuilder('albumPermissions')
      .leftJoinAndSelect('albumPermissions.album', 'album')
      .where('albumPermissions.userId = :hostId', { hostId })
      .andWhere('album.userId = :userId', { userId })
      .getMany();

    return albumsPermissions;
  }

  async addImage(
    {
      imageId,
      albumId,
    }: {
      imageId: number;
      albumId: number;
    },
    userId: number,
  ) {
    const album = await getRepository(Album)
      .createQueryBuilder('album')
      .leftJoinAndSelect('album.images', 'images')
      .where('album.id = :albumId', { albumId })
      .andWhere('album.userId = :userId', { userId })
      .getOne();

    const isImageExists = album.images.find(({ id }) => id === imageId);

    if (isImageExists)
      throw new HttpException('Image already added', HttpStatus.FORBIDDEN);

    const image = await getRepository(Image)
      .createQueryBuilder('image')
      .where('image.id = :id', { id: imageId })
      .andWhere('image.userId = :userId', { userId })
      .getOne();

    album.images = [...album.images, image];

    try {
      await getRepository(Album).manager.save(album);
    } catch (error) {
      //console.log(error);
      throw new HttpException(
        'Image has not been added',
        HttpStatus.BAD_REQUEST,
      );
    }
    return `Image "${image.id}" has been added to album : ${album.name}`;
  }

  async removeImage(
    {
      imageId,
      albumId,
    }: {
      imageId: number;
      albumId: number;
    },
    userId: number,
  ) {
    const album = await getRepository(Album)
      .createQueryBuilder('album')
      .leftJoinAndSelect('album.images', 'images')
      .where('album.id = :albumId', { albumId })
      .andWhere('album.userId = :userId', { userId })
      .getOne();

    const image = album.images.find(({ id }) => id == imageId);

    if (!image)
      throw new HttpException('Image not in album', HttpStatus.FORBIDDEN);

    album.images = album.images.filter(({ id }) => image.id !== id);

    try {
      await getRepository(Album).manager.save(album);
    } catch (error) {
      throw new HttpException(
        'Image has not been deleted from album',
        HttpStatus.BAD_REQUEST,
      );
    }

    return `Image "${image.id}" deleted from album : ${album.name}`;
  }

  async addPermission(
    {
      userId,
      albumId,
    }: {
      userId: number;
      albumId: number;
    },
    hostId: number,
  ) {
    if (hostId == userId)
      throw new HttpException(
        'Can not add permission for yourself',
        HttpStatus.BAD_REQUEST,
      );

    const permission = await getRepository(AlbumPermissions)
      .createQueryBuilder('albumPermissions')
      .where('albumPermissions.albumId = :albumId', { albumId })
      .andWhere('albumPermissions.userId = :userId', { userId })
      .getOne();

    if (permission)
      throw new HttpException(
        'Permissions already added',
        HttpStatus.FORBIDDEN,
      );
    const { album, user } = await getAlbumPermissionData(albumId, userId);

    const values = {
      user,
      album,
    };

    try {
      await getConnection()
        .createQueryBuilder()
        .insert()
        .into(AlbumPermissions)
        .values(values)
        .execute();
    } catch (error) {
      throw new HttpException(
        'Album permissions has not been added',
        HttpStatus.BAD_REQUEST,
      );
    }
    return {
      message: `Album "${album.name}" is readable for user : ${user.login}`,
    };
  }

  async removePermission(
    {
      userId,
      albumId,
    }: {
      userId: number;
      albumId: number;
    },
    hostId: number,
  ) {
    if (hostId == userId)
      throw new HttpException(
        'Can not add permission for itself',
        HttpStatus.BAD_REQUEST,
      );

    const { album, user } = await getAlbumPermissionData(albumId, userId);

    try {
      await getConnection()
        .createQueryBuilder()
        .delete()
        .from(AlbumPermissions)
        .where('albumId = :albumId', { albumId })
        .andWhere('userId = :userId', { userId })
        .execute();
    } catch (error) {
      throw new HttpException(
        'Album permissions has not been deleted',
        HttpStatus.BAD_REQUEST,
      );
    }

    return {
      message: `Album "${album.name}" is no more readable for user : ${user.login}`,
    };
  }
}
