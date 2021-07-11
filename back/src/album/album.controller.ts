import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
  UseGuards,
  Request,
} from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { AlbumService } from './album.service';

@Controller('album')
export class AlbumController {
  constructor(private readonly albumService: AlbumService) {}

  @Post()
  @UseGuards(JwtAuthGuard)
  create(@Request() req, @Body() { name }: { name: string }) {
    return this.albumService.create(req.user.id, name);
  }

  @Get('user/:userId')
  @UseGuards(JwtAuthGuard)
  findByUserId(@Request() req, @Param('userId') userId: string) {
    return this.albumService.findByUserId(req.user.id, +userId);
  }

  @Get(':id')
  @UseGuards(JwtAuthGuard)
  findOne(@Request() req, @Param('id') id: string) {
    return this.albumService.findOne(req.user.id, +id);
  }

  @Patch(':id')
  @UseGuards(JwtAuthGuard)
  update(
    @Request() req,
    @Param('id') id: string,
    @Body() { name }: { name: string },
  ) {
    return this.albumService.update(req.user.id, +id, name);
  }

  @UseGuards(JwtAuthGuard)
  @Post('image/add')
  addImage(@Body() body: { albumId: number; imageId: number }, @Request() req) {
    return this.albumService.addImage(body, req.user.id);
  }

  @UseGuards(JwtAuthGuard)
  @Post('image/remove')
  removeImage(
    @Body() body: { albumId: number; imageId: number },
    @Request() req,
  ) {
    return this.albumService.removeImage(body, req.user.id);
  }

  @Delete(':id')
  @UseGuards(JwtAuthGuard)
  remove(@Request() req, @Param('id') id: string) {
    return this.albumService.remove(req.user.id, +id);
  }

  @UseGuards(JwtAuthGuard)
  @Post('permission/add')
  addPermission(
    @Body() body: { albumId: number; userId: number },
    @Request() req,
  ) {
    return this.albumService.addPermission(body, req.user.id);
  }
  @UseGuards(JwtAuthGuard)
  @Post('permission/delete')
  removePermission(
    @Body() body: { albumId: number; userId: number },
    @Request() req,
  ) {
    return this.albumService.removePermission(body, req.user.id);
  }
}
