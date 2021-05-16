package com.tn.assetmanagement.respository;

import java.util.Collection;
import java.util.Optional;

public interface FindRepository<I, T>
{
  Collection<T> findAll() throws RepositoryException;

  Optional<T> findForId(I id) throws RepositoryException;
}
