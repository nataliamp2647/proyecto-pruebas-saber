package co.unicauca.iso2.taller2.users.domain.service;

import co.unicauca.iso2.taller2.users.domain.User;

/**
 * Abstracción de la regla de negocio "¿es válido este registro de usuario?".
 * Separarla de User (SRP) y de AuthService (DIP) permite cambiar las reglas
 * de validación sin tocar ninguna otra clase.
 *
 * @author Mani
 */
public interface IUserValidator {

    ValidationResult validate(User user, String plainPassword);
}
