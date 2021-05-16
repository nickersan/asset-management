package com.tn.assetmanagement.respository;

public interface DeleteRepository<I>
{
  void delete(I id) throws RepositoryException;
}
