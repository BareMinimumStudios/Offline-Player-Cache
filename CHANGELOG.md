## Additions 🍎
- Hey, hey, I figured out how to do a multi-loader project! (sorta)
- This update is compatible for (hopefully) all loaders.

## Changes 🌽
- Rewrote the cache (again).
  - The cache now uses a record-based system for registering with a provided Codec.
  - Register your record using `OfflinePlayerCacheAPI#register`.
  - Get your cache using `OfflinePlayerCacheAPI#getCache`.
- *Proper documentation will arrive upon release.*