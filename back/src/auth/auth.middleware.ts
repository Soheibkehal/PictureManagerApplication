import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { jwtConstants } from './constants';
const jwtService = new JwtService({ secret: jwtConstants.secret });

@Injectable()
export class AuthMiddleware {
  constructor() {}

  verifyTokenMiddleware(req, res, next) {
    try {
      const token = String(req.header('authorization')).split(' ')[1];
      console.log()
      const result = jwtService.verify(token, { secret: jwtConstants.secret });
      if (result) {
        req.user = result;
        next();
      } else {
        res.status(401).send('JWT NOT VALID');
      }
    } catch (error) {
      res.status(401).send('JWT NOT VALID');
    }
  }
}
