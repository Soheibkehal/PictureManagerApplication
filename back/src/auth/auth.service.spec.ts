import { JwtModule } from '@nestjs/jwt';
import { PassportModule } from '@nestjs/passport';
import { Test, TestingModule } from '@nestjs/testing';
import { AuthService } from './auth.service';
import { jwtConstants } from './constants';
import { JwtStrategy } from './strategies/jwt.strategy';
import { LocalStrategy } from './strategies/local.strategy';
import typeorm = require('typeorm');
import { setTypeormRepoMocked } from '../mocks/typeorm';
import { userMocked, usersMocked } from '../mocks/user';
import { HttpException } from '@nestjs/common';
import { User } from 'src/user/entities/user.entity';
describe('AuthService', () => {
  let service: AuthService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [
        PassportModule,
        JwtModule.register({
          secret: jwtConstants.secret,
          signOptions: { expiresIn: '60000000s' },
        }),
      ],
      providers: [AuthService, LocalStrategy, JwtStrategy],
      exports: [AuthService, JwtModule],
    }).compile();

    service = module.get<AuthService>(AuthService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('validateUser', () => {
    beforeAll(() => {
      typeorm.getRepository = setTypeormRepoMocked(usersMocked, userMocked);
    });
    it('should log in', async () => {
      expect(await service.validateUser('test', 'testtest')).toBe(userMocked);
    });

    it('should not log in, wrong password', async () => {
      try {
        await service.validateUser('test', 'wrong password');
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
    });

    it('should not log in because user not found', async () => {
      typeorm.getRepository = setTypeormRepoMocked(usersMocked, null);
      try {
        await service.validateUser('wrong mail', 'test');
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
      }
    });
  });
});
