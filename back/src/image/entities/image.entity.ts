import * as moment from 'moment';

import {
  Column,
  PrimaryGeneratedColumn,
  Entity,
  ManyToOne,
  OneToMany,
  JoinTable,
  ManyToMany,
} from 'typeorm';

import { User } from '../../user/entities/user.entity';
import { ImagePermissions } from '../../permissions/entities/image-permissions.entity';
import { Album } from '../../album/entities/album.entity';
@Entity()
export class Image {
  @PrimaryGeneratedColumn()
  id: number;
  @Column()
  name: string;
  @Column({ type: 'timestamptz', default: moment().format() })
  timestamp: Date;
  @Column()
  metadata: string;
  @ManyToOne(() => User, (user) => user.images)
  user: User;
  @ManyToMany(() => Album)
  @JoinTable()
  albums: Album[];
  @OneToMany(
    () => ImagePermissions,
    (imagePermissions) => imagePermissions.image,
  )
  imagePermissions: ImagePermissions[];
}
