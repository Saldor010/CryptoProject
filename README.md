There will be a better name for this project in the future, but for now..
# CryptoProject

This is my attempt at a [steganography](https://en.wikipedia.org/wiki/Steganography) based encryptor. This encryptor can take any file and hide it within the least significant bits of a .bmp file, then later retrieve said file. In addition, the encryptor can hide that data even further by leaving gaps between each bit of data, which can be changed through the offset setting.

Planned features include adding a password system (along with a proper encryption library) to add an extra layer of encryption, as well as a file explorer pop up to search for the input file as opposed to having to type in the path. I also plan on adding in command line support at some point.

I don't know how good this is compared to other similar programs, I just wanted to make this to see if I could. I originally got the idea from Cicada 3301 where they had to decrypt a text file from an image. Most of the code for this project was written in June of 2018, with only slight tweaking afterwards. If you want to fork this or do something with it, feel free. I plan on adding to this later, but who knows.
