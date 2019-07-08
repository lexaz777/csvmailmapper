package datamapper;

public interface DataMapper<T> {
    void put(T object);

    T get(int id);

    void update(T object);

    void delete(T object);
}
