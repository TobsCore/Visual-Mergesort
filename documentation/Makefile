TEX=xelatex
BIB=bibtex
BIBOUT=Bibliograph
OUT=Ausarbeitung
DOCUMENTATION=documentation

all: $(OUT)

bib: $(BIBOUT)

$(DOCUMENTATION): $(OUT)

$(OUT): 
	$(TEX) $(DOCUMENTATION)
	mv $(DOCUMENTATION).pdf $(OUT).pdf

$(BIBOUT):
	$(TEX) $(DOCUMENTATION)
	$(BIB) $(DOCUMENTATION).aux
	$(TEX) $(DOCUMENTATION)
	make $(OUT)

nclean: 
	$(nclean)

clean:
	$(nclean) ./*.pdf

nclean:= rm -f *.aux *.bbl *.bcf *.blg *.lof *.log *.out *.toc
