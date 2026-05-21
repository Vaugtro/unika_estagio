# TODO

## Bugs

### 1. Red highlight not showing on invalid inputs
- `is-invalid` class is applied correctly (visible in AJAX response)
- But Bootstrap's red border visual doesn't appear
- Custom CSS with `!important` added to `BasePage.html` — needs testing
- Possibly related to `<form>` inside `<tr>` (invalid HTML)?

### 2. Ativo/Inativo toggle button doesn't update dynamically
- Clicking the button updates the database correctly
- But the visual appearance (button color, status text) doesn't change until page reload
- Fix attempted: replaced raw JS DOM manipulation with `model.setEstaAtivo()` + `target.add(form)`
- Needs testing

## Improvements (deferred)

- [ ] Evaluate DTO vs FormModel tradeoffs (see `SUMMARY.md`)
- [ ] Validar se o Editar do jurídico funciona corretamente
