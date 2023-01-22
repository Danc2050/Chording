# Chorder Root/"Fix" List Generator
Quick points:
- This repository keeps a list of prefixes and suffixes (infixes to come) and show which words can be chorded based on 
  these "fixs".
- For emitting a chorded output with no space following the chord.
- I envision that by chording via prefixes, infixes, suffixes, and roots, one can reduce, by a significant amount, the number of 
  chords one has to memorize to grasp the skill of chording in the English language for most of their typing.

# Theory
According to Google, there are ~120 root words which originate from Greek and Latin
Over 60 percent of all English words have Greek or Latin roots. In the vocabulary of the sciences and technology, the figure rises to over 90 percent. [source](https://www.dictionary.com/e/word-origins)

## Credit
Some modification of prefix and suffix list from this [gist](https://gist.github.com/kevinflo/5cfe332a7eb2239a6da6).
Pretty comprehensive list of roots (1020 of them, not considering prefix/infix/suffix duplicates): https://membean.com/roots/
Holy grail of Latin and Greek roots in English language (oh my goodness): https://en.wikipedia.org/wiki/List_of_Greek_and_Latin_roots_in_English
Extreme amountt of words here: https://kaikki.org/dictionary/English/index.html

Java 2 Kotlin conversion from this [code](https://github.com/javiermartinezruiz/SearchPrefixInDictionary/blob/master/src/com/company/Main.java) 
to find words that match a prefix (to show usefulness of chording in "fixs").
## Future work 
Just a loose idea here. More prefixes, infixes, and suffixes should be added to completely grasp the English language.

Additionally, the same should be done for other languages if speakers of those languages so wish.

Add things that are not roots...E.g., "micr" is a root, but "micro" is the (common) prefix. Here is a good starting point:
https://www.readingrockets.org/article/root-words-roots-and-affixes

These may also be useful to parse and include: 
1) https://en.wikipedia.org/wiki/List_of_medical_roots,_suffixes_and_prefixes
2) https://en.wikipedia.org/wiki/List_of_commonly_used_taxonomic_affixes

The `pom.xml` is cluttered as I copied from another project.
