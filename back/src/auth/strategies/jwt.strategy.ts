import { ExtractJwt, Strategy } from 'passport-jwt';
import { PassportStrategy } from '@nestjs/passport';
import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { jwtConstants } from '../constants';
import { getRepository } from 'typeorm';
import { User } from '../../user/entities/user.entity';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor() {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: jwtConstants.secret,
    });
  }

  async validate(payload: Record<string, string | number>) {
    const user = await getRepository(User)
      .createQueryBuilder('user')
      .where('user.mail = :mail', { mail: payload.mail })
      .andWhere('user.id = :id', { id: payload.id })
      .getOne();
    if (!user) {
      throw new HttpException('Invalid token', HttpStatus.FORBIDDEN);
    }
    return { id: payload.id, mail: payload.mail };
  }
}
