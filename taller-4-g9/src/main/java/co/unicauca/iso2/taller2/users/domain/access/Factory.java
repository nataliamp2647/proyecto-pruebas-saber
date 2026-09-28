package co.unicauca.iso2.taller2.users.domain.access;

/**
 * Fábrica (Singleton) que se encarga de instanciar UserRepository o
 * cualquier otra implementación de IUserRepository que se cree en el
 * futuro, replicando el patrón usado en el ejemplo 5 de Inversión de
 * Dependencias (Factory de IProductRepository).
 *
 * @author Mani
 */
public class Factory {

    private static Factory instance;

    private Factory() {
    }

    public static Factory getInstance() {
        if (instance == null) {
            instance = new Factory();
        }
        return instance;
    }

    /**
     * Crea una instancia concreta de la jerarquía IUserRepository.
     *
     * @param type cadena que indica qué tipo de clase hija debe instanciar
     * @return una clase hija de la abstracción IUserRepository
     */
    public IUserRepository getRepository(String type) {
        IUserRepository result = null;
        switch (type) {
            case "default":
                result = new UserRepository();
                break;
        }
        return result;
    }
}
