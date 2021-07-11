import { PrimaryGeneratedColumn, Entity, ManyToOne } from 'typeorm';

import { User } from '../../user/entities/user.entity';
import { Album } from '../../album/entities/album.entity';

@Entity()
export class AlbumPermissions {
  @PrimaryGeneratedColumn()
  id: number;
  @ManyToOne(() => Album, (album) => album.albumPermissions)
  album: Album;
  @ManyToOne(() => User, (user) => user.albumPermissions)
  user: User;
}
