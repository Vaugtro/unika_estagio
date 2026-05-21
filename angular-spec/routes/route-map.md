# Route Map — Wicket → Angular

## Wicket URL Patterns

| Method | Wicket Route | Angular Route | Component |
|--------|-------------|---------------|-----------|
| `mountPage("/", ...)` | `/` | `/` | `HomeComponent` |
| `mountPage("/clientes/detalhe/${clienteId}", ...)` | `/clientes/detalhe/1` | `/fisico/:id` | `FisicoDetailComponent` |
| (not mounted — only accessible via link) | — | `/juridico/:id` | `JuridicoDetailComponent` |

## Angular Route Configuration (Standalone)

```typescript
// app.routes.ts
export const routes: Routes = [
  {
    path: '',
    component: AppLayoutComponent,
    children: [
      {
        path: '',
        loadComponent: () => import('./home/home.component').then(m => m.HomeComponent),
        title: 'Home - Clientes'
      },
      {
        path: 'fisico',
        loadChildren: () => import('./cliente-fisico/fisico.routes').then(m => fisicoRoutes),
        title: 'Clientes Físicos'
      },
      {
        path: 'juridico',
        loadChildren: () => import('./cliente-juridico/juridico.routes').then(m => juridicoRoutes),
        title: 'Clientes Jurídicos'
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
```

```typescript
// cliente-fisico/fisico.routes.ts
export const fisicoRoutes: Routes = [
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: ':id', component: FisicoDetailComponent }
];
```

```typescript
// cliente-juridico/juridico.routes.ts
export const juridicoRoutes: Routes = [
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: ':id', component: JuridicoDetailComponent }
];
```

## Page Parameter Mapping

| Wicket | Angular |
|--------|---------|
| `PageParameters().set("clienteId", id)` | `ActivatedRoute.snapshot.paramMap.get('id')` |
| `params.get("clienteId").toLong()` | `Number(route.snapshot.paramMap.get('id'))` |

## Navigation (BookmarkablePageLink equivalent)

| Wicket | Angular |
|--------|---------|
| `new BookmarkablePageLink<>("detalhesBtn", ClienteDetalhePage.class, new PageParameters().set("clienteId", id))` | `[routerLink]="['/fisico', id]"` |
| `setResponsePage(HomePage.class)` | `router.navigate(['/'])` |
| `target.add(panelContainer)` (AJAX) | Signal update → template re-render |
