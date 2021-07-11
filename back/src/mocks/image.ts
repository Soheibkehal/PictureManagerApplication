export const fileMocked: Express.Multer.File = {
  filename: 'test.png',
  originalname: 'test.png',
  fieldname: 'test',
  encoding: '',
  mimetype: 'jpeg',
  size: 30439,
  stream: null,
  destination: './test',
  path: './test',
  buffer: null,
};

export const imageMocked = {
  id: 0,
  name: 'test.png',
  timestamp: new Date(),
  metadata: '',
  userId: 0,
  imagePermissions: [],
};

export const imagesMocked = [imageMocked];
