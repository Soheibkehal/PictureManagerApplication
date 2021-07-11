import { PrimaryGeneratedColumn, Entity, ManyToOne } from 'typeorm';

import { User } from '../../user/entities/user.entity';
import { Image } from '../../image/entities/image.entity';

@Entity()
export class ImagePermissions {
  @PrimaryGeneratedColumn()
  id: number;
  @ManyToOne(() => Image, (image) => image.imagePermissions)
  image: Image;
  @ManyToOne(() => User, (user) => user.imagePermissions)
  user: User;
}
