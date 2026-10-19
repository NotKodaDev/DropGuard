# DropGuard

Plugin **Paper** permettant d'éviter le drop accidentel d'items au sol.

Lorsqu'un joueur (y compris admin) appuie sur sa touche de drop (**Q**), l'item n'est pas jeté tout de suite : un message de confirmation s'affiche et il doit **rappuyer** sur la touche dans la fenêtre définie pour confirmer et réellement jeter l'item tenu.

- **Auteur** : NotKoda_
- **API** : Paper 1.21.x (JDK 21)
- **PluginManager** : compatible PlugMan (rechargement à chaud)

## Compiler

```bash
mvn clean package
```

Le `.jar` est généré dans `target/dropguard.jar` → à placer dans `plugins/` du serveur Paper.

## Configuration (`config.yml`)

| Clé | Défaut | Description |
|-----|--------|-------------|
| `confirm-window-seconds` | `3` | Durée (s) pendant laquelle un second appui confirme le drop. |
| `confirm-message` | message | Message affiché au premier appui (`{item}` = item tenu, code couleur `&`). |

## Permission

| Permission | Défaut | Description |
|------------|--------|-------------|
| `dropguard.bypass` | `false` | Désactive la confirmation (drop direct) pour les joueurs qui l'ont. |
