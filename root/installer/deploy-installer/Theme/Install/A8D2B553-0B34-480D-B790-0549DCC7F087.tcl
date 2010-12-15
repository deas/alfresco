proc CreateWindow.A8D2B553-0B34-480D-B790-0549DCC7F087 {wizard id} {
    set base  [$wizard widget get $id]
    set frame $base.titleframe

    grid rowconfigure    $base 3 -weight 1
    grid columnconfigure $base 0 -weight 1

    frame $frame -bd 0 -relief flat -background white
    grid  $frame -row 0 -column 0 -sticky nsew

    grid rowconfigure    $frame 1 -weight 1
    grid columnconfigure $frame 0 -weight 1

    Label $frame.title -background white -anchor nw -justify left -autowrap 1  -font TkCaptionFont -textvariable [$wizard variable $id -text1]
    grid $frame.title -row 0 -column 0 -sticky new -padx 5 -pady 5
    $id widget set Title -widget $frame.title

    Label $frame.subtitle -background white -anchor nw -autowrap 1  -justify left -textvariable [$wizard variable $id -text2]
    grid $frame.subtitle -row 1 -column 0 -sticky new -padx [list 20 5]
    $id widget set Subtitle -widget $frame.subtitle

    label $frame.icon -borderwidth 0 -background white -anchor c
    grid  $frame.icon -row 0 -column 1 -rowspan 2
    $id widget set Icon -widget $frame.icon -type image

    Separator $base.separator -relief groove -orient horizontal
    grid $base.separator -row 1 -column 0 -sticky ew 

    frame $base.clientarea
    grid  $base.clientarea -row 3 -sticky nsew -padx 8 -pady 4
    $id widget set ClientArea -widget $base.clientarea -type frame

    grid rowconfigure    $base.clientarea 9 -weight 1
    grid columnconfigure $base.clientarea 0 -weight 1

    ttk::label $base.clientarea.label -text "Temporary Data Location"
    grid $base.clientarea.label -row 0 -column 0 -sticky w -padx 10 -pady 2

    ttk::entry $base.clientarea.entry -textvariable ::info(DEPDATA)
    grid $base.clientarea.entry -row 1 -column 0 -sticky new -padx {10 0} -pady 1

    ttk::button $base.clientarea.browse -text "..." -width 3 -command [list ::InstallAPI::PromptForDirectory -virtualtext DEPDATA]
    grid $base.clientarea.browse -row 1 -column 1 -sticky nw -padx {0 10}

    ttk::label $base.clientarea.label2 -text "Log Location (Where to store log information)"
    grid $base.clientarea.label2 -row 2 -column 0 -sticky w -padx 10 -pady 2

    ttk::entry $base.clientarea.entry2 -textvariable ::info(DEPLOG)
    grid $base.clientarea.entry2 -row 3 -column 0 -sticky new -padx {10 0} -pady 1

    ttk::button $base.clientarea.browse2 -text "..." -width 3 -command [list ::InstallAPI::PromptForDirectory -virtualtext DEPLOG]
    grid $base.clientarea.browse2 -row 3 -column 1 -sticky nw -padx {0 10}

    ttk::label $base.clientarea.label3 -text "Metadata Location (Where to store metadata)"
    grid $base.clientarea.label3 -row 4 -column 0 -sticky w -padx 10 -pady 2

    ttk::entry $base.clientarea.entry3 -textvariable ::info(DEPMETA)
    grid $base.clientarea.entry3 -row 5 -column 0 -sticky new -padx {10 0} -pady 1

    ttk::button $base.clientarea.browse3 -text "..." -width 3 -command [list ::InstallAPI::PromptForDirectory -virtualtext DEPMETA]
    grid $base.clientarea.browse3 -row 5 -column 1 -sticky nw -padx {0 10}

    ttk::label $base.clientarea.label4 -text "Target Location (where to put deployed files)"
    grid $base.clientarea.label4 -row 6 -column 0 -sticky w -padx 10 -pady 2

    ttk::entry $base.clientarea.entry4 -textvariable ::info(DEPROOTDIR)
    grid $base.clientarea.entry4 -row 7 -column 0 -sticky new -padx {10 0} -pady 1

    ttk::button $base.clientarea.browse4 -text "..." -width 3 -command [list ::InstallAPI::PromptForDirectory -virtualtext DEPROOTDIR]
    grid $base.clientarea.browse4 -row 7 -column 1 -sticky nw -padx {0 10}

    ttk::label $base.clientarea.label5 -text "Name of default file system target"
    grid $base.clientarea.label5 -row 8 -column 0 -sticky w -padx 10 -pady 2

    ttk::entry $base.clientarea.entry5 -textvariable ::info(DEPFSNAME)
    grid $base.clientarea.entry5 -row 9 -column 0 -sticky new -padx {10 0} -pady 1
}

