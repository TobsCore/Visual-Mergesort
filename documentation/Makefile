TEX=xelatex
OUT=Ausarbeitung
DOCUMENTATION=documentation

all: $(OUT)

$(DOCUMENTATION): $(OUT)

$(OUT): 
	$(TEX) $(DOCUMENTATION)

nclean: 
	$(nclean)

clean:
	$(nclean) ./*.pdf

nclean:= rm -f *.aux *.bbl *.bcf *.blg *.lof *.log *.out *.toc
