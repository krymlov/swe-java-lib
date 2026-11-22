# swe-java-lib

swe-java-lib is based on sources of Swiss Ephemeris (Astrodienst AG) 

Sources copied from: 
- http://www.astro.com/ftp/swisseph
- http://www.th-mack.de/international/download/

Added files to:
- src/main/java/org/swisseph

Modified some files within:
- src/main/java/swisseph

## Ephemeris files

Do not mix `sepl*` / `seas*` files of different Swiss Ephemeris releases in one ephemeris
folder (or in one `swe_set_ephe_path()` / `SE_EPHE_PATH`). The files of 2.10.04 have a different
layout (Jupiter and Saturn of order 40, 6000-day Saturn segments); with the stale-segment defect
of the library after a file switch, a mixed set gives Jupiter and Saturn wrong by degrees
(confirmed by Astrodienst, November 2026). Replace the whole set at once. The `ephe/` folder here
holds the 2.10.03 files only.


# Swiss Ephemeris License

Please make sure before you use the project you are familiar with the Swiss Ephemeris License
- https://www.astro.com/swisseph/swephinfo_e.htm
- https://www.astro.com/swisseph/secont_e.pdf

If you want the Swiss Ephemeris Free Edition for your software project, please proceed as follows:
- make sure you understand the License conditions
- download the software
- start programming