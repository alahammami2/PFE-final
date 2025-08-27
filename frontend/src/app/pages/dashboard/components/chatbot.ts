import { Component, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  standalone: true,
  selector: 'app-chatbot',
  imports: [CommonModule, FormsModule, HttpClientModule],
  template: `
    <div class="card h-[48vh] md:h-[56vh] flex flex-col relative">
      <!-- Header -->
      <div class="flex items-center justify-between pb-3 border-b border-gray-200">
        <div class="font-semibold text-xl">Assistant</div>
        <div class="flex items-center gap-2">
          <button type="button" class="p-button p-component p-button-outlined p-button-sm" (click)="newChat()">
            <i class="pi pi-plus mr-2"></i>
            <span class="hidden sm:inline">Nouveau chat</span>
          </button>
          <span *ngIf="loading" class="text-xs text-muted-color">Envoi...</span>
        </div>
      </div>

      <!-- Messages -->
      <div class="flex-1 overflow-auto py-4 space-y-3" #scrollArea>
        <ng-container *ngIf="messages.length; else empty">
          <div *ngFor="let m of messages" class="flex" [ngClass]="{ 'justify-end': m.role === 'user', 'justify-start': m.role === 'assistant' }">
            <div
              class="max-w-[75%] rounded-2xl px-4 py-2 text-sm whitespace-pre-wrap"
              [ngClass]="{
                'bg-blue-600 text-white rounded-br-none': m.role === 'user',
                'bg-gray-100 text-gray-900 rounded-bl-none': m.role === 'assistant'
              }"
            >
              {{ m.text }}
            </div>
          </div>
        </ng-container>
        <ng-template #empty>
          <div class="text-sm text-gray-500 text-center mt-6">Posez votre question .</div>
        </ng-template>
      </div>

      <!-- Composer -->
      <div class="pt-3 border-t border-gray-200">
        <div class="flex items-end gap-2">
          <textarea
            class="p-inputtextarea p-inputtext p-component flex-1 resize-none rounded-xl shadow-sm border border-gray-300 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 px-4 py-3"
            rows="2"
            placeholder="Écrire un message..."
            [(ngModel)]="question"
            (keydown.enter)="onEnter($event)"
            (input)="onInput($event)"
          ></textarea>
          <button
            class="h-11 w-11 rounded-full flex items-center justify-center bg-blue-600 text-white shadow-sm ring-1 ring-blue-600/10 hover:bg-blue-700 active:bg-blue-700/90 disabled:bg-gray-300 disabled:text-white disabled:cursor-not-allowed transition"
            [disabled]="loading || !question?.trim()"
            (click)="send()"
            type="button"
            title="Envoyer"
            aria-label="Envoyer"
          >
            <i class="pi pi-send text-sm"></i>
          </button>
        </div>
        <div *ngIf="error" class="text-red-500 text-xs mt-2">{{ error }}</div>
      </div>
    </div>
  `
})
export class Chatbot implements AfterViewInit {
  question = '';
  loading = false;
  error: string | null = null;
  messages: { role: 'user' | 'assistant'; text: string }[] = [];
  @ViewChild('scrollArea') private scrollArea?: ElementRef<HTMLDivElement>;

  constructor(private http: HttpClient) {}

  ngAfterViewInit(): void {
    // Ensure we start at bottom if there are initial messages
    setTimeout(() => this.scrollToBottom(), 0);
  }

  private push(role: 'user' | 'assistant', text: string) {
    this.messages.push({ role, text });
    this.scrollToBottom();
  }

  private scrollToBottom() {
    const el = this.scrollArea?.nativeElement;
    if (!el) return;
    // Use rAF for smoothness after view updates
    requestAnimationFrame(() => {
      el.scrollTop = el.scrollHeight;
    });
  }

  onInput(e: Event) {
    const ta = e.target as HTMLTextAreaElement;
    // Auto-resize
    ta.style.height = 'auto';
    ta.style.height = Math.min(148, ta.scrollHeight) + 'px';
  }

  onEnter(event: Event) {
    const ke = event as KeyboardEvent;
    if (ke.shiftKey) {
      // Allow newline
      return;
    }
    ke.preventDefault();
    this.send();
  }

  newChat() {
    this.messages = [];
    this.question = '';
    this.error = null;
    this.loading = false;
    this.scrollToBottom();
  }

  send(): void {
    const q = (this.question || '').trim();
    if (!q || this.loading) return;

    this.push('user', q);
    this.question = '';
    this.loading = true;
    this.error = null;

    const url = `${environment.chatbotGatewayUrl}/ask`;
    this.http.post(url, { question: q }, { responseType: 'text' }).subscribe({
      next: (res) => {
        const a = res ?? '';
        this.push('assistant', a as string);
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Une erreur est survenue.';
        const msg = this.error || 'Une erreur est survenue.';
        this.push('assistant', msg);
        this.loading = false;
      }
    });
  }
}