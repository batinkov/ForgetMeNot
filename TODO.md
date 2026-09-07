# ForgetMeNot — what's next

Ranked. See `CLAUDE.md` for how the project works and what's already settled.

## Next

**Language.** The data is Bulgarian, every string in the app is English —
`SUNDAY`, `18 January`, `Turning 34`, `COMING UP`, `In memory`. Weekday and
month names come from `dayOfWeek.name`, the enum constant, so they cannot
localise at all as written. The most visible gap.

**Ordering within a day.** Deferred early, now visible: 17 September shows four
cards in file order, which is arbitrary. Personal before public, or actionable
first? Worth folding into the language pass — ordering and wording are the same
pass over the card.

**Android.** "Don't forget" fails on a machine you aren't sitting at. The
biggest fork; everything else is smaller. Also the prerequisite for
notifications, which is the feature that makes the app work rather than merely
exist.

**The editor, and writable storage.** One job: the editor needs somewhere to
write, which means moving off the bundled resource to a platform data directory
and splitting personal events from the calendar. That split is also what
decides what gets backed up — see the storage notes in the design discussion.

**Dark mode.** Never discussed. The palette is light-only; a desktop app that
ignores the system theme stands out.

## Known gaps

**The focus refresh in `App.kt` is untested.** `refreshToday()` beneath it is
well covered, but the `LaunchedEffect` that calls it is not — and the wiring
layer is exactly what broke silently once before, when `App()` was called
without its argument and a default parameter hid it. Testing a composable needs
`compose-ui-test`, which is a dependency decision.

**Neither scroll area announces itself beyond the fade.** No scrollbar. Fine so
far; revisit if the fade turns out to be too subtle in use.

## Done

**Midnight rollover.** Three triggers keep the date honest: a once-a-night
delay, a refresh when the window regains focus, and cold start. Guarded so it
will not move you if you have navigated elsewhere, and cannot clear done-state
on a same-day tick.

## Tried and rejected

**System tray.** Compose Desktop has `Tray`/`rememberTrayState`, and
`SystemTray.isSupported()` returns true on this machine, so the icon appears —
but it is dead. Logged handlers proved it: closing the window fired our
`onCloseRequest`, while double-click and right-click fired neither `onAction`
nor the menu. KDE bridges AWT's legacy XEmbed tray icons through
`xembedsniproxy`, which renders the icon and animates it on click but never
delivers the event to AWT's listeners; AWT's `PopupMenu` is an X11 popup that
does not draw through the proxy either. Not fixable in our code.

Reverted, because hide-on-close was a trap with no way back to the window.
Closing exits again and the tray is gone; the drawn icon was kept as the window
icon (`AppIcon.kt`).

To revisit: `dorkbox:SystemTray` speaks StatusNotifierItem over DBus directly
rather than going through AWT, and would give a real left-click and menu — a new
dependency, so a developer decision. Worth doing only after Android, since the
tray exists to serve desktop notifications and notifications matter far more on
a phone.
