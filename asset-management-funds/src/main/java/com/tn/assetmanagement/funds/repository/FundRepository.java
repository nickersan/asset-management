package com.tn.assetmanagement.funds.repository;

import java.util.Collection;
import java.util.Optional;

import com.tn.assetmanagement.funds.domain.Fund;

public interface FundRepository
{
  Collection<Fund> findAll() throws RepositoryException;

  Optional<Fund> findForId(int id) throws RepositoryException;

  Integer save(Fund fund) throws RepositoryException;

  void delete(int id) throws RepositoryException;
}
