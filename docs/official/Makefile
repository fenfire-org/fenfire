# skr-2003.dvi: skr-2003.tex Makefile
skr-2003.dvi::
	TEXINPUTS=.:../../../navidoc-depends/ucs:../../../navidoc-depends/ucs/data: latex skr-2003.tex
	BIBINPUTS=../../../manuscripts bibtex skr-2003
	TEXINPUTS=.:../../../navidoc-depends/ucs:../../../navidoc-depends/ucs/data: latex skr-2003.tex
	TEXINPUTS=.:../../../navidoc-depends/ucs:../../../navidoc-depends/ucs/data: latex skr-2003.tex


skr-2003.pdf: skr-2003.tex Makefile
	TEXINPUTS=.:../../../navidoc-depends/ucs:../../../navidoc-depends/ucs/data: pdflatex skr-2003.tex
	BIBINPUTS=../../../manuscripts bibtex skr-2003
	TEXINPUTS=.:../../../navidoc-depends/ucs:../../../navidoc-depends/ucs/data: pdflatex skr-2003.tex
	TEXINPUTS=.:../../../navidoc-depends/ucs:../../../navidoc-depends/ucs/data: pdflatex skr-2003.tex


