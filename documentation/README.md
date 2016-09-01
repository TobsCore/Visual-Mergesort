## Documentation ##

In order to create a readable documentation, you need the following programs installed:

* make
* XeLatex
* Bibtex


### Compiling the documentation ###
If you have the necessary programs installed, you can create the documentation by running the following command:

```
> make bib
```
This will create the pdf will all the necessary bibliographical references. If you haven't changed any reference, and you simply want to compile the program, run:

```
> make all
```
or simply
```
> make
```

### Cleaning up ###
To delete the files that were created by the LaTeX compiler (i.e `.aux`, `.log` and `.toc` files), you can remove them by running
```
> make clean
```

This removes all unnecessary files, including the created output file (which is usually a `.pdf`). If you want to remove everything, except the `.pdf` output file, run
```
> make nclean
```

## Structure ##
To understand the folder structure, this section is here to help you. There should be only one folder: `images/`

This folder contains all the image resources, which will be used in the documentation.

The `Makefile` is used, to simplify the compile process.

The `documentation.tex` is the main `.tex` file, which is used for compilation. It includes all the other `.tex` files. It defines all packages and the layout. The content of the documentation is split up in the other `.tex` files, which improves the project structure.

The `sources.bib` is the bibtex file, which contains all the sources that are needed for citation purposes.
