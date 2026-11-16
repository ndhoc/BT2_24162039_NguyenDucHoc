/* Ô nhập OTP 6 chữ số dùng chung cho trang kích hoạt và đặt lại mật khẩu.
   Ô <input class="otp-fallback" name="code"> được chuyển thành hidden để vẫn
   gửi đúng một tham số "code"; 6 ô hiển thị chỉ là giao diện. */
(function () {
  'use strict';

  function build(wrapper) {
    var hidden = wrapper.querySelector('input.otp-fallback');
    if (!hidden) return;

    var boxes = document.createElement('div');
    boxes.className = 'otp-boxes';
    boxes.setAttribute('role', 'group');
    boxes.setAttribute('aria-label', 'Mã OTP gồm 6 chữ số');

    var inputs = [];
    for (var i = 0; i < 6; i++) {
      var box = document.createElement('input');
      box.type = 'text';
      box.inputMode = 'numeric';
      box.autocomplete = i === 0 ? 'one-time-code' : 'off';
      box.maxLength = 1;
      box.className = 'otp-box';
      box.id = 'otp-box-' + i;
      box.setAttribute('aria-label', 'Chữ số thứ ' + (i + 1));
      boxes.appendChild(box);
      inputs.push(box);
    }

    wrapper.appendChild(boxes);

    var label = wrapper.querySelector('label');
    if (label) label.setAttribute('for', 'otp-box-0');

    hidden.type = 'hidden';
    hidden.removeAttribute('required');
    hidden.removeAttribute('pattern');
    hidden.removeAttribute('inputmode');
    hidden.removeAttribute('maxlength');
    hidden.removeAttribute('autocomplete');
    hidden.removeAttribute('placeholder');
    hidden.removeAttribute('aria-label');

    function sync() {
      hidden.value = inputs.map(function (input) { return input.value; }).join('');
      inputs.forEach(function (input) {
        input.classList.toggle('filled', input.value !== '');
      });
    }

    function focusAt(index) {
      var target = inputs[Math.max(0, Math.min(inputs.length - 1, index))];
      target.focus();
      target.select();
    }

    function fill(value, startIndex) {
      var digits = value.replace(/\D/g, '');
      if (!digits) return;
      for (var i = 0; i < digits.length && startIndex + i < inputs.length; i++) {
        inputs[startIndex + i].value = digits.charAt(i);
      }
      sync();
      focusAt(startIndex + digits.length);
    }

    inputs.forEach(function (input, index) {
      input.addEventListener('input', function () {
        var digits = input.value.replace(/\D/g, '');
        input.value = digits.slice(-1);
        sync();
        if (input.value) focusAt(index + 1);
      });

      input.addEventListener('keydown', function (event) {
        if (event.key === 'Backspace') {
          event.preventDefault();
          if (input.value) {
            input.value = '';
            sync();
          } else if (index > 0) {
            inputs[index - 1].value = '';
            sync();
            focusAt(index - 1);
          }
        } else if (event.key === 'ArrowLeft') {
          event.preventDefault();
          focusAt(index - 1);
        } else if (event.key === 'ArrowRight') {
          event.preventDefault();
          focusAt(index + 1);
        } else if (event.key === 'Delete') {
          input.value = '';
          sync();
        }
      });

      input.addEventListener('paste', function (event) {
        var data = event.clipboardData || window.clipboardData;
        var text = data ? data.getData('text') : '';
        if (text) {
          event.preventDefault();
          fill(text, index);
        }
      });

      input.addEventListener('focus', function () { input.select(); });
    });

    var form = wrapper.closest('form');
    if (form) {
      form.addEventListener('submit', function (event) {
        sync();
        if (hidden.value.length !== 6) {
          event.preventDefault();
          focusAt(hidden.value.length);
        }
      });
    }

    if (hidden.value) fill(hidden.value, 0);
    focusAt(0);
  }

  function init() {
    Array.prototype.forEach.call(document.querySelectorAll('[data-otp]'), build);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
