import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
  UseInterceptors,
  UploadedFile,
  Request,
  UseGuards,
} from '@nestjs/common';
import { ImageService } from './image.service';
import { FileInterceptor } from '@nestjs/platform-express';
import { imageUploadConfig } from './helpers';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
@Controller('image')
export class ImageController {
  constructor(private readonly imageService: ImageService) {}

  @UseInterceptors(FileInterceptor('image', imageUploadConfig))
  @UseGuards(JwtAuthGuard)
  @Post()
  create(@Request() req, @UploadedFile() file: Express.Multer.File) {
    return this.imageService.create(req.user.id, file);
  }

  @UseGuards(JwtAuthGuard)
  @Get('user/:userId')
  findByUserId(@Param('userId') userId: string, @Request() req) {
    return this.imageService.findByUserId(+userId, req.user.id);
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.imageService.findOne(+id);
  }

  @UseGuards(JwtAuthGuard)
  @Delete(':id')
  remove(@Request() req, @Param('id') id: string) {
    return this.imageService.remove(req.user.id, +id);
  }

  @UseGuards(JwtAuthGuard)
  @Post('permission/add')
  addPermission(
    @Body() body: { imageId: number; userId: number },
    @Request() req,
  ) {
    return this.imageService.addPermission(body, req.user.id);
  }

  @UseGuards(JwtAuthGuard)
  @Post('permission/delete')
  removePermission(
    @Body() body: { imageId: number; userId: number },
    @Request() req,
  ) {
    return this.imageService.removePermission(body, req.user.id);
  }
}
