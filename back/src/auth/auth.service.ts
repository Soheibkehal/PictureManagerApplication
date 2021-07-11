import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { User } from '../user/entities/user.entity';
import { getRepository } from 'typeorm';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';
import { jwtConstants } from './constants';

@Injectable()
export class AuthService {
  constructor(private jwtService: JwtService) {}
  async validateUser(mail: string, password: string) {
    const user = await getRepository(User)
      .createQueryBuilder('user')
      .where('user.mail = :mail', { mail })
      .getOne();
    const checkedPassword = await bcrypt.compare(
      password,
      user?.password || '',
    );

    if (!user || !checkedPassword)
      throw new HttpException(
        { message: 'Wrong password or wrong mail' },
        HttpStatus.FORBIDDEN,
      );
    return user;
  }

  async login(credentials: { username: string; password: string }) {
    const user = await getRepository(User)
      .createQueryBuilder('user')
      .where('user.mail = :mail', { mail: credentials.username }) //can't change to mail, username property is mandatory
      .getOne();

    const samePass = bcrypt.compareSync(
      credentials.password,
      user?.password || '',
    );
    if (!samePass || !user)
      throw new HttpException(
        { message: 'Wrong password or wrong mail' },
        HttpStatus.FORBIDDEN,
      );

    const payload = { id: user.id, mail: user.mail };
    return {
      accessToken: this.jwtService.sign(payload),
      userId: user.id,
    };
  }
}
