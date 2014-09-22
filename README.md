pixel-sorting
============
Sorts the pixels in an image in an (hopefully) aesthetically-pleasing way.

The program takes in a source image file and outputs the pixel-sorted image into a folder named "PSResultsXX" where *XX* is a timestamp.

There are some sample pixel-sorted images in the Samples folder.

How to run
============
> java Runner *file* [--top] [--right] [--bottom] [--left] [--seed]

- **--top**, **--right**, **--bottom**, **--left** Sort from top-down, right-to-left, bottom-up, or left-to-right. Will overwrite each other. Defaults to *--bottom*.
- **--seed** Sets the seed on the random number using a timestamp

(Requires Java 7)
