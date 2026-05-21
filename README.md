# BaatCheet GitHub Pages Website

Static public website for BaatCheet, a secure, lightweight chat and direct messaging app developed by IJ Roy.

## Pages

- Home: `./`
- Account deletion: `./account-deletion/`
- Privacy policy: `./privacy-policy/`
- Terms and conditions: `./terms-and-conditions/`
- Community guidelines: `./community-guidelines/`
- Child safety standards: `./child-safety-standards/`
- Contact: `./contact/`

## Play Console account deletion URL

After GitHub Pages is enabled for `ij-roy/baatcheet`, use:

`https://ij-roy.github.io/baatcheet/account-deletion/`

## Local verification

Run the static checks:

```powershell
powershell -ExecutionPolicy Bypass -File tools\verify-static-site.ps1
```

Serve locally from this directory:

```powershell
python -m http.server 8080
```

Then test:

- `http://localhost:8080/`
- `http://localhost:8080/account-deletion/`
- `http://localhost:8080/privacy-policy/`
- `http://localhost:8080/terms-and-conditions/`
- `http://localhost:8080/community-guidelines/`
- `http://localhost:8080/child-safety-standards/`
- `http://localhost:8080/contact/`

No build step is required.
