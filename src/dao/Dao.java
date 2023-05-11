package dao;

public interface Dao<Obj,Id> {
    public void insert(Obj object) throws DaoException;
    public void update(Obj object) throws DaoException;
    public void delete(Id id) throws DaoException;
    public Object get(Id id) throws DaoException;
}
