# Project Guidelines

## Wicket Development
- **Framework Version**: Wicket 7.17.0
- **Design Patterns**:
    - Must not use `@SpringBean` with `transient`.
    - Use `StatelessSession` for stateless pages.
    - Use `Page` for the main application page.
    - Use `Panel` for reusable components.
    - Use `WebMarkupContainer` for non-visual components.
    - Use `Form` for forms.
    - Use `AjaxFormComponentUpdatingBehavior` for form submission handling.
    - Use `AjaxRequestTarget` for AJAX updates.
    - Use `AjaxFallback...` components for graceful degradation when AJAX is disabled.
    - Use `AjaxLink` for links that trigger AJAX updates.
    - Use `AjaxBehavior` for behaviors that trigger AJAX updates.
    - Everything must follow Wicket coding conventions and SOLID principles.
- **Component Encapsulation**: 
    - Components should be self-contained and encapsulated.
    - Components extending `Panel` MUST have their own HTML template.
    - For `WebMarkupContainer` or `Form`, use composition to add sub-components.
- **Model Management**:
    - **Always use models!** Do not pass raw objects directly to components.
    - Database entities MUST use `LoadableDetachableModel` to manage data loading and state lifecycle, ensuring compatibility with Wicket's stateful page and AJAX lifecycle.
    - Use `CompoundPropertyModel` for form binding.
    - Do not unwrap models in constructors. Unpack models in event calls like `onClick`, `onSubmit`, or `onBeforeRender`.
- **Form Handling**:
    - Validators must NOT change data or models.
    - Use `AjaxButton`/`AjaxSubmitLink` for AJAX form submissions.
    - When using `AjaxButton` with `setDefaultFormProcessing(true)`, validation is performed.
- **AJAX Integration**:
    - Use `AjaxRequestTarget` to refresh components (`target.add(component)`).
    - Ensure components to be updated via AJAX have `setOutputMarkupId(true)`.
    - Use `AjaxFormComponentUpdatingBehavior` for triggering model updates on input events.
    - Use `AjaxFallback...` components (e.g., `AjaxFallbackLink`, `AjaxFallbackButton`) for graceful degradation when AJAX is disabled.
    - Be aware that AJAX components/behaviors make the hosting page stateful.
- **Spring Integration & DB**:
    - Use `wicket-spring` module.
    - Register `SpringComponentInjector` during application initialization to enable injection.
    - Use `@SpringBean` annotation to inject Spring-managed beans into Wicket components.
    - *Note*: Wicket does not directly manage Spring DB connections; these are handled by Spring-managed beans (e.g., DataSources, Transaction Managers) which can be injected into Wicket components via `@SpringBean`.
- **General Best Practices**:
    - Avoid monolithic constructors; extract logic into `onInitialize()` or small, well-named methods.
    - Do not pass components to constructors (use dependency injection for services).
    - Every page and component MUST be tested using `WicketTester`.



