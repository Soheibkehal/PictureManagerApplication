import { HttpException, HttpStatus } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';

export const checkCredentialFields = (
  userDto: CreateUserDto | UpdateUserDto,
) => {
  const loginLength = userDto.login.length;
  if (loginLength < 2 || loginLength > 50)
    throw new HttpException('Login length is incorrect', HttpStatus.FORBIDDEN);

  const pwdLength = userDto.password.length;
  if (pwdLength < 8 || pwdLength > 50)
    throw new HttpException(
      'Password length is incorrect',
      HttpStatus.FORBIDDEN,
    );
};
