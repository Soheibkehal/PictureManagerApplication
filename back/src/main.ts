import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import * as express from 'express';
import { join } from 'path';
import { AuthMiddleware } from './auth/auth.middleware';

const authMiddleware = new AuthMiddleware();

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  try {
    app.use(
      '/media',
      authMiddleware.verifyTokenMiddleware,
      express.static(join(process.cwd(), 'uploads')),
    );
  } catch (error) {
    console.log(error);
  }
  await app.listen(8080);
}

bootstrap();
