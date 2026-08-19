# Licence trećih strana

## Inter (font)

Fajlovi u `src/commonMain/composeResources/font/Inter-*.ttf` su statičke instance fonta
**Inter** (https://github.com/rsms/inter), generisane iz zvaničnog varijabilnog fonta
objavljenog u Google Fonts repozitorijumu (`ofl/inter/Inter[opsz,wght].ttf`) alatom
`fonttools` (`varLib.instancer`, osa `wght` fiksirana na 400/500/600/700, `opsz` na 14).

Statička instanca je namerna: Compose Multiplatform na Kotlin/Wasm cilju ima poznat
problem sa renderovanjem varijabilnih fontova (prikazuju se kao prazni kvadratići), pa
je font unapred "zapečen" na tačno one debljine koje aplikacija koristi.

Licenca: SIL Open Font License 1.1 — vidi `Inter-OFL.txt`.

Font je lokalno ugrađen (nema eksternih URL-ova ni Google Fonts CDN-a) — aplikacija radi
offline, u skladu sa pravilom projekta da nema spoljašnjih mrežnih poziva.
