export const setTypeormRepoMocked = (
  getManyMocked: Record<string, unknown>[] = undefined,
  getOneMocked: Record<string, unknown> = undefined,
) =>
  jest.fn().mockReturnValue({
    createQueryBuilder: jest.fn().mockReturnValue({
      getMany: jest.fn().mockReturnValue(getManyMocked),
      getOne: jest.fn().mockResolvedValue(getOneMocked),
      where: jest.fn().mockReturnThis(),
      andWhere: jest.fn().mockReturnThis(),
      leftJoinAndSelect: jest.fn().mockReturnThis(),
      insert: jest.fn().mockReturnThis(),
      into: jest.fn().mockReturnThis(),
      values: jest.fn().mockReturnThis(),
      execute: jest.fn().mockReturnThis(),
      update: jest.fn().mockReturnThis(),
      set: jest.fn().mockReturnThis(),
      delete: jest.fn().mockReturnThis(),
      from: jest.fn().mockReturnThis(),
    }),
  });
