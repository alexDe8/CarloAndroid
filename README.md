# CarloVault

CarloVault is an Android password manager prototype built with Jetpack Compose.

The first version focuses on a high-impact security UI:

- Security cockpit with breach, reuse, weak-password and 2FA signals.
- Vault list with search, category filters and entry-level risk badges.
- Password generator with length, symbols, readability, passphrase and one-time-share flags.
- Control center with many security flags: biometrics, passkeys, screen shield, clipboard wipe, travel mode, decoy vault, emergency access, hardware keys, offline mode, encrypted backup, phishing checks and more.

The current data is local sample state inside `MainActivity.kt`. The natural next step is replacing that state with encrypted persistence using Android Keystore, SQLCipher or Jetpack Security, then wiring real biometric unlock and breach-check services.
