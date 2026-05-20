# TODO

## Compilation Errors
- [ ] `ClienteFisicoServiceImpl.findAll(Pageable)` return type `Page<ClienteFisicoListResponse>` is not compatible with `AbstractClienteService.findAll(Pageable)` return type `Page<ClienteFisico>`
- [ ] `ClienteJuridicoServiceImpl.findAll(Pageable)` return type `Page<ClienteJuridicoListResponse>` is not compatible with `AbstractClienteService.findAll(Pageable)` return type `Page<ClienteJuridico>`
  - Fix: remove `findAll` from `AbstractClienteService` since each service maps entities to response DTOs differently at the controller level, OR change `AbstractClienteService.findAll` to return `Page<T>` and have services expose their typed `findAll` via the interface only (remove from abstract class)

## Service Layer
- [ ] Decide whether `AbstractClienteService.findAll` should exist or if each service impl should own its `findAll` via the interface contract only
- [ ] `ClienteFisicoServiceImpl.activate/inactivate` uses `entityManager.flush() + detach()` — verify this matches the abstract contract

## Wicket / Create Modals
- [ ] Wire `ClienteFisicoCreateModal.onSubmit` to call `clienteFisicoService.create(dto)` (DTO mapping not yet implemented)
- [ ] Wire `ClienteJuridicoCreateModal.onSubmit` to call `clienteJuridicoService.create(dto)` (DTO mapping not yet implemented)

## Cleanup / Verification
- [ ] Run `gradlew compileJava` after fixes to confirm build passes
- [ ] Run `gradlew test` to verify existing tests still pass
- [ ] Verify `AbstractClienteService` doesn't duplicate method signatures that conflict with concrete service interface contracts
