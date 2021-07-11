import { Image } from '../../image/entities/image.entity';
import { User } from '../../user/entities/user.entity';
import {
  Column,
  PrimaryGeneratedColumn,
  Entity,
  ManyToMany,
  JoinTable,
  ManyToOne,
  OneToMany,
} from 'typeorm';

import { AlbumPermissions } from '../../permissions/entities/album-permissions.entity';
@Entity()
export class Album {
  @PrimaryGeneratedColumn()
  id: number;
  @Column()
  name: string;
  @Column({ type: 'timestamptz' })
  timestamp: Date;
  @ManyToOne(() => User, (user) => user.images)
  user: User;
  @OneToMany(
    () => AlbumPermissions,
    (albumPermissions) => albumPermissions.album,
  )
  albumPermissions: AlbumPermissions[];
  @ManyToMany(() => Image)
  @JoinTable()
  images: Image[];
}
