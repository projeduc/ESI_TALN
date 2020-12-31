%
%    Copyright (C) 2017  Abdelkrime Aries <kariminfo0@gmail.com>
%

\NeedsTeXFormat{LaTeX2e} %[2009/09/24]
\ProvidesClass{KodeBook}[2017/08/17 Create a book with codes]
\LoadClass[12pt, a4paper]{book}

\raggedbottom

%\RequirePackage[utf8]{inputenc}
%\RequirePackage[arabic,french,english]{babel}
%\RequirePackage{CJKutf8}

\RequirePackage[noxcolor]{beamerarticle}
\RequirePackage[table]{xcolor}

\RequirePackage{fancyhdr}
\RequirePackage{fontenc}
\RequirePackage[utf8]{inputenc}
\RequirePackage{amsmath,amssymb,wasysym} 
\RequirePackage[japanese,french]{babel}
\RequirePackage{arabtex,acolor}
\RequirePackage{txfonts}
\RequirePackage{tipa}
\RequirePackage{xeCJK}

\setCJKmainfont{IPAMincho}
\setCJKsansfont{IPAGothic}
\setCJKmonofont{IPAGothic}


\RequirePackage{multirow}
\RequirePackage{array}

\RequirePackage{tikz}
\RequirePackage{environ}
%\RequirePackage{cmap}
%\RequirePackage{ruby}
\RequirePackage{xstring}

\definecolor{indigo}{RGB}{75,0,130}%indigo
%\definecolor{purpleX11}{RGB}{160,32,240}
\definecolor{Crimson}{rgb}{0.6471, 0.1098, 0.1882}


\def\ptop{2cm}
\def\pleft{3cm}
\def\pright{1cm}
\def\pbottom{2cm}

%\pagenumbering{arabic} % needed even though this class will not show page numbers
\pagestyle{empty}


\RequirePackage[left=\pleft,right=\pright,top=\ptop,bottom=\pbottom]{geometry}

\RequirePackage[Glenn]{fncychap}
\ChTitleVar{\bfseries\Large\scshape\color{indigo}}
\ChNumVar{\bfseries\Huge\color{indigo}}
\ChNameVar{\bfseries\Large\color{indigo}}

\RequirePackage{etoolbox}

%\makeatletter
\patchcmd{\@makechapterhead}{\vspace*{50\p@}}{\vspace*{-40\p@}}{}{}
\patchcmd{\@makeschapterhead}{\vspace*{50\p@}}{\vspace*{-40\p@}}{}{}
\patchcmd{\DOTI}{\vskip 80\p@}{\vskip 20\p@}{}{}
\patchcmd{\DOTIS}{\vskip 40\p@}{\vskip 0\p@}{}{}
%Color
\patchcmd{\DOCH}{\mghrulefill}{\color{black}\mghrulefill}{}{}
\patchcmd{\DOTIS}{\mghrulefill}{\color{black}\mghrulefill}{}{}
\patchcmd{\DOTI}{\mghrulefill}{\color{black}\mghrulefill}{}{}
%\makeatother

%Quotes, introduction
\RequirePackage[many]{tcolorbox}


\def\thesection{\arabic{section}}

\renewcommand{\@listI}{%
	\leftmargin=25pt
	\rightmargin=0pt
	\labelsep=5pt
	\labelwidth=20pt
	\itemindent=0pt
	\listparindent=0pt
	\topsep=0pt plus 2pt minus 4pt
	\partopsep=0pt plus 1pt minus 1pt
	\parsep=0pt plus 1pt
	\itemsep=\parsep
}


\RequirePackage{fontspec}
\defaultfontfeatures{Mapping=tex-text}

\setmainfont
[Path = fonts/CrimsonText/,
Extension = .ttf,
Ligatures=TeX,
UprightFont=*-Regular,
BoldFont=*-Bold,
ItalicFont=*-Italic,
BoldItalicFont=*-BoldItalic
]{CrimsonText} %

\newfontfamily\euphoria
[Path = fonts/EuphoriaScript/,
Extension = .ttf,
Ligatures=TeX,
UprightFont=*-Regular,
Scale=2
]{EuphoriaScript} %




\newfontface\quotefont
[Path = fonts/Handlee/,
Extension = .ttf,
Ligatures=TeX,
Scale=1.
]{Handlee-Regular} %



\newfontface\codefont
[Path = fonts/SourceCodePro/,
Extension = .ttf,
Ligatures=TeX
%Scale=1.5
]{SourceCodePro-Bold} %

%https://www.dafont.com/decorated-roman-ini.font?l[]=1
%Decorated Roman Initials by Dieter Steffmann
%First seen on DaFont: before 2005
\newfontface\lettrinefont
[Path = fonts/decoratedRomanIni/,
Extension = .ttf,
Ligatures=TeX
%Scale=1.5
]{DecoratedRomanInitials} %


\newtcolorbox{kodequote}[1]{%
colback=white,
colframe=black,
colbacktitle=indigo!10!white,
coltitle=indigo,
fontupper=\quotefont,
title={#1}
}

\newtcolorbox{introduction}{
	enhanced jigsaw,
	oversize,
	rightrule=0pt,
	toprule=0pt,
	bottomrule=0pt,
	leftrule=8pt,
	colback=white,
	colframe=indigo,
	arc=0pt,
	outer arc=0pt,
%	title={Remarque :},
%	title style={white},
%	fonttitle=\color{black}\bfseries,
	titlerule=0pt,
	bottomtitle=0pt,
	top=0pt,
	bottom=0pt,
	left=0pt,
	enlarge left by=.25\textwidth,
	width=0.75\textwidth,
	fontupper=\itshape
}

\newenvironment{discussion}{%
	\section*{Discussion}
	\addcontentsline{toc}{section}{Discussion}
	\markright{Discussion}
}{%
}


\renewcommand{\baselinestretch}{1}

\setlength\parindent{0pt}
\setlength{\parskip}{\baselineskip}%


\RequirePackage{listings}
\RequirePackage{lstlangKB}
\RequirePackage{textcomp} %for textasciitilde

\def\@codeStyle@background{\color{indigo!10!white}}

\lstdefinestyle{codeStyle}{
	belowcaptionskip=1\baselineskip,
	breaklines=true,
	frame=L,
	xleftmargin=1.5cm,%\parindent,
	showstringspaces=false,
	basicstyle=\scriptsize\ttfamily\bfseries, %\codefont,
	keywordstyle=\bfseries\color{blue!70!black}, %green!40!black
	keywordstyle = [2]{\bfseries\color{orange!70!black}},
	keywordstyle = [3]{\bfseries\color{red!70!black}},
	commentstyle=\itshape\color{black!70!white}, %purple!40!black
	identifierstyle=\color{black},%blue
	stringstyle=\color{green!50!black},
	backgroundcolor=\@codeStyle@background ,
	lineskip=.2em,
	numbers=left
	%  frameround=tttt
}

\def\@shellStyle@background{\color{black}}
\def\@shellStyle@basicstyle{\scriptsize\codefont\color{green}}

\lstdefinestyle{shellStyle}{
	belowcaptionskip=1\baselineskip,
	breaklines=true,
	frame=trbl,
%	rulesepcolor=\color{blue},
	xleftmargin=1.25cm,
	basicstyle=\@shellStyle@basicstyle ,
	backgroundcolor=\@shellStyle@background ,
	lineskip=.2em
	%	morekeywords={sudo},keywordstyle=\color{red},
	%  frameround=tttt
}

%\DeclareRobustCommand\keyword{\@ifnextchar[{\@@keyword}{\@keyword}}
\def\@keyword#1{{\itshape\bfseries\color{red} #1}}

%\def\@@keyword[#1]#2{\lowercase{\def\temp{#2}}%
%\@keyword#2\expandafter\index\expandafter{#1!\temp}}

\definecolor{lightblue}{HTML}{D0D2FF}
\definecolor{lightyellow}{HTML}{FFFFAA}
\definecolor{darkblue}{HTML}{0000BB}
\definecolor{olivegreen}{HTML}{006600}
\definecolor{violet}{HTML}{6600CC}

%\newcommand{\keyword}[1]{\@keyword{#1}\index{#1}}
\newcommand{\expword}[1]{\textcolor{olivegreen}{#1}}
\newcommand{\optword}[1]{\textcolor{violet}{\bfseries #1}}

\usepackage{ifthen}

\def\@escapeall@idx#1#2\relax{"\uppercase{#1}\ifx\relax#2\relax\else\@@escapeall@idx#2\relax\fi}
\def\@@escapeall@idx#1#2\relax{"#1\ifx\relax#2\relax\else\@@escapeall@idx#2\relax\fi}

\def\camelcase#1#2\relax{\uppercase{#1}\ifx\relax#2\relax\else\@camelcase#2\relax\fi}
\def\@camelcase#1#2\relax{#1\ifx\relax#2\relax\else\@camelcase#2\relax\fi}

\newcommand{\keyword}[2][]{%
	\@keyword{#2}%
	\ifthenelse { \equal{#1}{} }%
	{}%
%	{\expandafter\index\expandafter{#1!\@escapeall@idx#2\relax}}%
%	{\@@keyword[#1]#2}
	{\lowercase{\def\temp{#2}}%
		\@keyword#2\expandafter\index\expandafter{#1!\temp}}
}

\def\nameword#1{{\itshape\bfseries\color{red} #1}}

%=======================================================
%===================== COVER ===========================
%=======================================================

\setcounter{page}{0}

\newtoks\@coverimage
\@coverimage={}

\def\cover#1
{
	\IfFileExists{#1}{
		\@coverimage={\noindent\includegraphics[width=\paperwidth, height=\paperheight]{#1} } %
	}{
	\@coverimage={}
}
}

\newtoks\@licenseimage
\@licenseimage={}

\def\license#1
{
	\IfFileExists{#1}{
		\@licenseimage={\noindent\includegraphics[width=4cm]{#1} } %
	}{
	\@licenseimage={}
}
}

\newcounter{coverind}

\def\replace#1{%
	\setcounter{coverind}{0}
	\begingroup
	\edef\@tempa{#1 }%
	\expandafter\endgroup
	\expandafter\replaceit\@tempa\relax
}

\def\replaceit#1 #2\relax{%replace blanks in a string
	#1
	\begingroup
	\ifx\relax#2\relax  % is #2 empty?
	\def\next{\endgroup\endit}% your own end-macro if required
	\else
	\def\next{\endgroup\\[0.25cm]\hspace{\arabic{coverind}cm}\replaceit#2\relax}%
	\stepcounter{coverind}%
%	\addtocounter{coverind}{5}
	\fi
	\next
}
\def\endit{}


\def\maketitle {
	\phantomsection
	\addcontentsline{toc}{part}{\@title}
	\begin{tikzpicture}[remember picture,overlay]
	\ifx\isplain\relax\else
	\node[inner sep=0] at (current page.center) {\the\@coverimage};
	\fi
	
	\begin{scope}[every node/.style={rotate=35}]
	\node[anchor=north west,minimum width=\textwidth,minimum height=40cm,fill=indigo] (vline1)
	at ([shift={(3cm,0cm)}] current page.north west)
	{};
	\end{scope}

	\node[anchor=north west, text=white,text width =15cm,opacity=0,text opacity=1,scale=1.5] (title1)
	at ([shift={(0.4\textwidth,-1.5cm)}] current page.north west)
	{
		\bfseries
		\Huge
%		\merienda
%		\@title
		\euphoria
		\replace\@title
	};

	\node[anchor=south west,minimum width=2\textwidth,minimum height=2.4cm,fill=white] (authorline)
	at ([shift={(0cm,0cm)}] current page.south west)
	{};


	\node[text width=8cm,anchor=north west, align=left] at ([shift={(1cm, 1.7cm)}] current page.south west){
		\bfseries
		\Huge
		\ifx\isplain\relax\else \color{indigo}\fi
		\@author
	};

	\node[text=white,text width=8cm,anchor=north west] at ([shift={(-5cm,2cm)}] current page.south east){
		\the\@licenseimage
	};
	\end{tikzpicture}
}

%fancyhdr horizontal rule
\renewcommand{\headrulewidth}{0pt}

\def\frontmatter{
	\cleardoublepage
	\pagenumbering{roman}
	\pagestyle{fancy}
	\phantomsection
	\addcontentsline{toc}{part}{Pages préliminaires}
	\fancyhf{} % sets both header and footer to nothing
	\fancyfoot[CE, CO]{\thepage}
%	\fancyhead{}
}


\def\backmatter{
	\cleardoublepage
%	\pagenumbering{roman}
%	\pagestyle{empty}
	\phantomsection
	\addcontentsline{toc}{part}{Pages complémentaires}
	%\fancyfoot{}
	%\fancyhead{}

	\fancyhf{}
%	\fancyfoot[CE, CO]{}

	\fancyhead{\color{indigo}\thepage\hrulefill}

}

\renewcommand{\mainmatter}{

	\cleardoublepage

	\pagenumbering{arabic}
	\phantomsection
	\addcontentsline{toc}{part}{Corps}

	\pagestyle{fancy}                       % Sets fancy header and footer
%	\fancyfoot{}                            % Delete current footer settings

	\fancyhf{}


	\fancyhead[E]{
		\begin{tikzpicture}[remember picture,overlay]
		\node[anchor=north west, minimum width=\textwidth+\pright-2pt, minimum height=0.5cm,rectangle,very thick,draw=black] (bx1)
		at ([shift={(0cm,-1cm)}] current page.north west){};
%
		\node[anchor=west, fill=white,minimum width=1cm]
		at ([shift={(\pright,0cm)}] bx1.north west)
		{\large \color{indigo}\textbf{\thepage}};
%
		\node[anchor=east,fill=white,align=left]
		at ([shift={(2pt,0cm)}] bx1.south east)
		{\color{indigo}\textbf{\nouppercase{\rightmark}}};
		\end{tikzpicture}
	}

	\fancyhead[O]{
		\begin{tikzpicture}[remember picture,overlay]
		\node[anchor=north west, minimum width=\textwidth+\pright-2pt, minimum height=0.5cm,rectangle,very thick,draw=black] (bx1)
		at ([shift={(\pleft,-1cm)}] current page.north west){};
		%
		\node[anchor=east, fill=white,minimum width=1cm,align=left]
		at ([shift={(-\pright,0cm)}] bx1.north east)
		{\large \color{indigo}\textbf{\thepage}};
%		%
		\node[anchor=west,fill=white]
		at ([shift={(-2pt,0cm)}] bx1.south west)
		{\color{indigo}\textbf{\nouppercase{\leftmark}}};
		\end{tikzpicture}
	}


	\fancypagestyle{plain}{% % <-- this is new
		\fancyhf{}
		\fancyfoot[LE,RO]{} % same placement as with page style "fancy"
	}
}


\RequirePackage{tocloft}

\renewcommand{\contentsname}{}
\renewcommand{\listfigurename}{}
\renewcommand{\listtablename}{}

\renewcommand{\cftpartfont}{\normalfont\sffamily\bfseries}% \part font in ToC
\renewcommand{\cftchapfont}{\normalfont\large\itshape}    % \chapter font in ToC
\renewcommand{\cftsecfont}{\normalfont\slshape}           % \section font in ToC

\def\kodetoc{%

	\chapter*{Table des matières}
	\addcontentsline{toc}{section}{Table des matières}

	{
		\hypersetup{linkcolor=black}
		\tableofcontents
	}
}

\def\kodelof{%

	\chapter*{Liste des figures}
	\addcontentsline{toc}{section}{Liste des figures}
	{
		\hypersetup{linkcolor=black}
		\listoffigures
	}

}

\def\kodelot{%

	\chapter*{Liste des tableaux}
	\addcontentsline{toc}{section}{Liste des tableaux}
		{
			\hypersetup{linkcolor=black}
			\listoftables
		}

}

\RequirePackage{acro}
%\RequirePackage[acronyms]{glossaries}
%\glsenablehyper
%\acsetup{tooltip=true}
%\acsetup{
%	page-ref    = none,% Seitennummer in Klammern
%	extra-style = comma,% extra-Informationen mit Komma anhÃ€ngen
%	only-used   = false,% fÃŒr das Beispiel auch die nicht verwendeten in die Liste aufnehmen
%	sort        = true,  % Liste sortieren
%	hyperref  = {true}
%}

%\ExplSyntaxOn
%\cs_set_protected:Npn \acro_activate_hyperref_support:
%{
%	\bool_if:nT { \l__acro_hyperref_loaded_bool && \l__acro_use_hyperref_bool }
%	{
%		\sys_if_engine_xetex:TF
%		{
%			\cs_set:Npn \acro_hyper_link:nn ##1##2
%			{ \hyperlink { \XeTeXLinkBox {##1} } {##2} }
%		}
%		{ \cs_set_eq:NN \acro_hyper_link:nn \hyperlink }
%		\cs_set:Npn \acro_hyper_target:nn ##1##2
%		{ \raisebox { 3ex } [ 0pt ] { \hypertarget {##1} { } } ##2 }
%	}
%}
%\ExplSyntaxOff

\def\kodeabbrev{%

	\chapter*{Liste des abréviations}
	\addcontentsline{toc}{section}{Liste des abréviations}

	\printacronyms[include-classes=abbrev,name=]
%	\printglossaries
%	\printglossary[type=\acronymtype]
%	\printglossary

}

\RequirePackage{imakeidx}

\indexsetup{level=\chapter*,toclevel=chapter}

\makeindex[program=makeindex,columns=2, options={-s KodeBook.ist}]

%\makeindex

\setcounter{tocdepth}{1}

\RequirePackage{lettrine}

\renewcommand{\LettrineFontHook}{\color{indigo}\lettrinefont}
\renewcommand{\LettrineTextFont}{\color{indigo}\bfseries}
\setlength{\DefaultNindent}{2pt}
\setlength{\DefaultFindent}{2pt}
\setlength{\DefaultSlope}{0pt}
\setcounter{DefaultLines}{3}
%\renewcommand{\DefaultLraise}{0.05}
\renewcommand{\DefaultLoversize}{0.1}

%\newcommand{\at}{\makeatletter @\makeatother}

\RequirePackage[bookmarksdepth=3,pdfusetitle,colorlinks,urlcolor=blue]{hyperref}

\newcommand{\langlogo}[3]{%
	\begin{wrapfigure}{l}{2.25cm}
		\centering
		\vspace{#2}
		\includegraphics[width=2cm]{#1}
		\vspace{#3}
	\end{wrapfigure}
}


\newcommand\setplainversion{

	\definecolor{indigo}{RGB}{0,0,0}%indigo

	\hypersetup{
		hidelinks
	}

	\renewcommand{\lettrine}[2]{##1##2}

	\renewcommand\merienda{}
	\renewcommand\quotefont{}

	\def\@keyword##1{{\itshape\bfseries ##1}}

	\def\nameword##1{``{\itshape\bfseries ##1}"}

	\def\@codeStyle@background{\color{white}}
	\def\@shellStyle@background{\color{white}}
	\def\@shellStyle@basicstyle{\scriptsize\codefont\color{black}}

%	\selectcolormodel{gray}

	\renewcommand{\langlogo}[3]{}

	\let\isplain=\relax
}


\def\graphpath{}

\newcommand{\changegraphpath}[1]{\def\graphpath{#1}}


\newcommand{\vgraphpage}[2][.84\textheight]{%
	%	\begin{center}%
	\includegraphics[height=#1]{\graphpath #2}%
	%	\end{center}%
}

\newcommand{\hgraphpage}[2][\textwidth]{%
	%	\begin{center}%
	\includegraphics[width=#1]{\graphpath #2}%
	%	\end{center}%
}

\newcommand{\graphpage}[2][]{%
	\includegraphics[#1]{\graphpath #2}%
}

\makeatletter
\newcommand{\xRightarrow}[2][]{\ext@arrow 0359\Rightarrowfill@{#1}{#2}}
\makeatother 
