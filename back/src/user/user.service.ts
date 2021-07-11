import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { getRepository, getConnection } from 'typeorm';
import { User } from './entities/user.entity';
import { ImagePermissions } from '../permissions/entities/image-permissions.entity';
import { checkCredentialFields } from './helpers';
import * as bcrypt from 'bcrypt';
const saltRounds = 10;

@Injectable()
export class UserService {
  async create(createUserDto: CreateUserDto) {
    checkCredentialFields(createUserDto);

    if (!createUserDto.mail.includes('@') && createUserDto.mail.length < 4)
      throw new HttpException('mail is incorrect', HttpStatus.FORBIDDEN);

    const user = await getRepository(User)
      .createQueryBuilder('user')
      .where('user.mail = :mail', { mail: createUserDto.mail })
      .getOne();
    if (user)
      throw new HttpException(
        'User already exists with this mail',
        HttpStatus.FORBIDDEN,
      );

    try {
      const hashedPassword = bcrypt.hashSync(
        createUserDto?.password,
        saltRounds,
      );
      createUserDto.password = hashedPassword;

      await getConnection()
        .createQueryBuilder()
        .insert()
        .into(User)
        .values(createUserDto)
        .execute();
    } catch (error) {
      throw new HttpException(
        'User has not been created',
        HttpStatus.BAD_REQUEST,
      );
    }

    return { message: `User ${createUserDto.login} has been created` };
  }

  async findAll() {
    const users = await getRepository(User)
      .createQueryBuilder('user')
      .getMany();

    users.forEach((user) => {
      delete user?.password;
    });
    return users;
  }

  async findOne(id: number) {
    const user = await getRepository(User)
      .createQueryBuilder('user')
      .where('user.id = :id', { id })
      .getOne();

    if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND);

    delete user?.password;
    return user;
  }

  async getFeed(id: number) {
    const user = await getRepository(User)
      .createQueryBuilder('user')
      .where('user.id = :id', { id })
      .getOne();

    if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND);

    const imagePermissions = await getRepository(ImagePermissions)
      .createQueryBuilder('imagePermissions')
      .leftJoinAndSelect('imagePermissions.image', 'image')
      .leftJoinAndSelect('image.user', 'user')
      .where('imagePermissions.userId = :userId', { userId: user.id })
      .getMany();

    return imagePermissions?.map(({ image }) => image);
  }

  async update(id: number, updateUserDto: UpdateUserDto) {
    checkCredentialFields(updateUserDto);

    const user = await getRepository(User)
      .createQueryBuilder('user')
      .where('user.id = :id', { id })
      .getOne();

    if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND);

    try {
      await getConnection()
        .createQueryBuilder()
        .update(User)
        .set(updateUserDto)
        .where('id = :id', { id })
        .execute();
    } catch (error) {
      throw new HttpException(
        'User has not been updated',
        HttpStatus.BAD_REQUEST,
      );
    }

    return `User ${updateUserDto.login} has been updated (id: ${id})`;
  }

  async remove(id: number) {
    const user = await getRepository(User)
      .createQueryBuilder('user')
      .where('user.id = :id', { id })
      .getOne();
    if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND);

    try {
      await getConnection()
        .createQueryBuilder()
        .delete()
        .from(User)
        .where('id = :id', { id })
        .execute();
    } catch (error) {
      throw new HttpException(
        'User has not been deleted',
        HttpStatus.BAD_REQUEST,
      );
    }

    return `User ${id} has been deleted`;
  }
}
