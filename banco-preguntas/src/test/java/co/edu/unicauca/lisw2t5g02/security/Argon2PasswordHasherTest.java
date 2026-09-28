package co.edu.unicauca.lisw2t5g02.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Argon2PasswordHasherTest {
    private final Argon2PasswordHasher hasher=new Argon2PasswordHasher();
    @Test void createsAndVerifiesArgon2idHash(){String hash=hasher.hash("ClaveSegura123!");assertTrue(hash.startsWith("$argon2id$"));assertTrue(hasher.verify("ClaveSegura123!",hash));assertFalse(hasher.verify("otraClave",hash));assertFalse(hasher.needsRehash(hash));}
    @Test void verifiesSha256AndRequestsUpgrade(){Sha256PasswordHasher old=new Sha256PasswordHasher();String legacy=old.hash("ClaveSegura123!");assertTrue(hasher.isHashed(legacy));assertTrue(hasher.verify("ClaveSegura123!",legacy));assertTrue(hasher.needsRehash(legacy));}
}
