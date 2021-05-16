package com.tn.assetmanagement.respository;

public interface SaveRepository<I, T>
{
  I save(T entity) throws RepositoryException;
}
