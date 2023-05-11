package dao;

import model.ModelProduct;

public interface DaoProduct extends Dao<ModelProduct, String>{

    @Override
    public void insert(ModelProduct object) throws DaoException;

    @Override
    public void update(ModelProduct object) throws DaoException;

    @Override
    public void delete(String id) throws DaoException;

    @Override
    public Object get(String id) throws DaoException;
    
    
}
