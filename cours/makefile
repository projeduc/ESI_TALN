# -*- makefile -*-

FILE = *.tex

CMD = xelatex -synctex=1 -interaction=nonstopmode taln.tex

allmake: $(FILE)

	$(CMD) #run xelatx 
	bibtex taln.aux
	makeindex taln
	$(CMD) #run xelatx 
	
	
clean: 
	rm -f *.synctex.gz *.log *.aux *.blg *.out *.qry *.nav *.snm *.toc *.lof *.lot *.glo *.acn *.up* *.idx *.ilg *.ind

clear: clean
	rm -f *.pdf *.bbl
#	clean
