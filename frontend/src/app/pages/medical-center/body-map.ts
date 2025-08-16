import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
    selector: 'app-body-map',
    standalone: true,
    imports: [CommonModule],
    template: `
        <svg
            [attr.width]="width"
            [attr.height]="height"
            viewBox="0 0 80 160"
            xmlns="http://www.w3.org/2000/svg"
            class="select-none"
        >
            <!-- Head -->
            <circle cx="40" cy="18" r="10" [attr.fill]="getFill('head')" (click)="onClick('head')" [style.cursor]="clickable ? 'pointer' : 'default'" />

            <!-- Torso -->
            <rect x="30" y="28" width="20" height="36" rx="6" [attr.fill]="getFill('chest')" (click)="onClick('chest')" [style.cursor]="clickable ? 'pointer' : 'default'" />
            <rect x="30" y="64" width="20" height="10" rx="4" [attr.fill]="getFill('abdomen')" (click)="onClick('abdomen')" [style.cursor]="clickable ? 'pointer' : 'default'" />

            <!-- Arms -->
            <rect x="20" y="30" width="8" height="34" rx="4" [attr.fill]="getFill('left-arm')" (click)="onClick('left-arm')" [style.cursor]="clickable ? 'pointer' : 'default'" />
            <rect x="52" y="30" width="8" height="34" rx="4" [attr.fill]="getFill('right-arm')" (click)="onClick('right-arm')" [style.cursor]="clickable ? 'pointer' : 'default'" />

            <!-- Thighs -->
            <rect x="30" y="74" width="8" height="30" rx="4" [attr.fill]="getFill('left-quad')" (click)="onClick('left-quad')" [style.cursor]="clickable ? 'pointer' : 'default'" />
            <rect x="42" y="74" width="8" height="30" rx="4" [attr.fill]="getFill('right-quad')" (click)="onClick('right-quad')" [style.cursor]="clickable ? 'pointer' : 'default'" />

            <!-- Knees badges -->
            <circle cx="34" cy="106" r="5" [attr.fill]="getFill('left-knee')" (click)="onClick('left-knee')" [style.cursor]="clickable ? 'pointer' : 'default'" />
            <circle cx="46" cy="106" r="5" [attr.fill]="getFill('right-knee')" (click)="onClick('right-knee')" [style.cursor]="clickable ? 'pointer' : 'default'" />

            <!-- Calves -->
            <rect x="30" y="112" width="8" height="28" rx="4" [attr.fill]="getFill('left-calf')" (click)="onClick('left-calf')" [style.cursor]="clickable ? 'pointer' : 'default'" />
            <rect x="42" y="112" width="8" height="28" rx="4" [attr.fill]="getFill('right-calf')" (click)="onClick('right-calf')" [style.cursor]="clickable ? 'pointer' : 'default'" />
        </svg>
    `
})
export class BodyMapComponent {
    @Input() highlightedZones: string[] = [];
    @Input() clickable: boolean = false;
    @Input() width = 60;
    @Input() height = 120;
    @Output() zoneToggle = new EventEmitter<string>();

    private defaultFill = '#e5e7eb'; // gray-200
    private activeFill = '#ef4444'; // red-500

    getFill(zone: string): string {
        return this.highlightedZones?.includes(zone) ? this.activeFill : this.defaultFill;
    }

    onClick(zone: string) {
        if (!this.clickable) return;
        this.zoneToggle.emit(zone);
    }
}


