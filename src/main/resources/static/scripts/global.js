"use strict";

import { callApiExpectNoBody, getCsrfToken, getCurrentAccount } from "./modules/client.js";
import { LOGGED_IN_SKEY, logoutParam, CSRF_HEADER_NAME, CURRENT_ACCOUNT_SKEY, USER_SETTINGS_SKEY } from "./modules/constants.js";

const menuBtn = document.getElementById("menu-btn");
menuBtn.addEventListener("click", () => {
  document.getElementById("nav-links").classList.toggle("active");
  const centered = document.querySelector(".centered-content");
  if (centered == null) {
    return;
  }
  console.log(`${centered.style.opacity}`);
  if (centered.style.visibility === "hidden") {
    centered.style.visibility = "visible";
    centered.style.opacity = "1";
  } else {
    centered.style.visibility = "hidden";
    centered.style.opacity = "0";
  }
});


const desktopMediaMatch = window.matchMedia("(min-width: 768px)");
desktopMediaMatch.addEventListener("change", (e) => {
  document.getElementById("nav-links").classList.remove("active");
  const centered = document.querySelector(".centered-content");
  if (centered == null) {
    return;
  }
  if (e.matches) {
    centered.style.visibility = "visible";
    centered.style.opacity = "1";
  }
});

const dropdownBtns = document.querySelectorAll(".dropdown-btn");
dropdownBtns.forEach((el) => {
  el.addEventListener("click", () => {
    const content = el.parentElement.querySelector(".dropdown-content");
    if (content.style.display === "none") {
      content.style.display = "block";
    } else {
      content.style.display = "none";
    }
  });
});

// const notDropdowns = document.querySelectorAll(":not(.dropdown, .dropdown *)");
// notDropdowns.forEach((elem) => {
//   elem.addEventListener("click", () => {
//     const content = document.querySelectorAll(".dropdown-content");
//     content.forEach((elem) => {
//       elem.style.display = "none";
//     });
//   });
// });

const logoutBtns = document.querySelectorAll(".logout-btn");
logoutBtns.forEach(el => {
  el.addEventListener("click", async () => {
    callApiExpectNoBody("/auth/logout", {
      method: "POST",
      headers: {
        [CSRF_HEADER_NAME]: await getCsrfToken(),
      }
    });
    sessionStorage.setItem(LOGGED_IN_SKEY, "false");
    sessionStorage.removeItem(CURRENT_ACCOUNT_SKEY);
    sessionStorage.removeItem(USER_SETTINGS_SKEY);
    window.location.replace(`/login?${logoutParam}`);
  });
});


document.addEventListener("DOMContentLoaded", () => {
  // const authNavlinks = document.querySelectorAll(".auth-navlinks:not(#account-menu)");
  const authNavlinks = document.querySelectorAll(".auth-navlinks");
  const anonNavlinks = document.querySelectorAll(".anon-navlinks");
  showHideAdminElements();
  if (sessionStorage.getItem(LOGGED_IN_SKEY) === "true") {
    anonNavlinks.forEach(elem => {
      elem.style.display = "none";
    });
    authNavlinks.forEach(elem => {
      elem.style.display = "block";
    });
  } else {
    anonNavlinks.forEach(elem => {
      elem.style.display = "block";
    });
    authNavlinks.forEach(elem => {
      elem.style.display = "none";
    });
  }
  if (window.innerWidth < 768) {
    document.querySelector("#account-menu").style.display = "none";
  }
});

desktopMediaMatch.addEventListener("change", (e) => {
  const accountMenu = document.querySelector("#account-menu");
  if (e.matches) {
    if (sessionStorage.getItem(LOGGED_IN_SKEY) === "true") {
      accountMenu.style.display = "block";
    } else {
      accountMenu.style.display = "none";
    }
  } else {
    accountMenu.style.display = "none";
  }
});

async function showHideAdminElements() {
  const adminNavlinks = document.querySelectorAll(".admin-navlink");
  const curAcc = await getCurrentAccount();
  console.debug(`Current account: ${JSON.stringify(curAcc)}`);
  if (sessionStorage.getItem(LOGGED_IN_SKEY) !== "true") {
    for (const elem of adminNavlinks) {
      elem.style.display = "none";
    }
  } else if (curAcc.roles.includes("ROLE_ADMIN")) {
    for (const elem of adminNavlinks) {
      elem.style.display = "block";
    }
  } else {
    for (const elem of adminNavlinks) {
      elem.style.display = "none";
    }
  }
}
