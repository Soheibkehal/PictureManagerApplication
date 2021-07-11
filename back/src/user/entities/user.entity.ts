import { AlbumPermissions } from '../../permissions/entities/album-permissions.entity';
import { ImagePermissions } from '../../permissions/entities/image-permissions.entity';
import { Column, PrimaryGeneratedColumn, Entity, OneToMany } from 'typeorm';
import { Image } from '../../image/entities/image.entity';
import { Album } from 'src/album/entities/album.entity';
@Entity()
export class User {
  @PrimaryGeneratedColumn()
  id: number;
  @Column()
  login: string;
  @Column()
  password: string;
  @Column()
  mail: string;
  @OneToMany(() => Image, (image) => image.user)
  images: Image[];
  @OneToMany(() => Album, (album) => album.user)
  albums: Image[];
  @OneToMany(
    () => ImagePermissions,
    (imagePermissions) => imagePermissions.user,
  )
  imagePermissions: ImagePermissions[];
  @OneToMany(
    () => AlbumPermissions,
    (albumPermissions) => albumPermissions.album,
  )
  albumPermissions: AlbumPermissions[];
}
