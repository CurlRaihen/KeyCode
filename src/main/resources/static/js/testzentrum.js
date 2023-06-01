import * as THREE from 'https://unpkg.com/three@0.120.1/build/three.module.js';
import { OrbitControls } from "https://unpkg.com/three@0.120.0/examples/jsm/controls/OrbitControls";
import { GLTFLoader } from "https://cdn.jsdelivr.net/npm/three@0.121.1/examples/jsm/loaders/GLTFLoader.js";

if (access == null) {
	access = [];
}

//const doors = ["1"];

//

var doormodelgreen = "images/doorgreen.gltf";
var doormodelred = "images/doorred.gltf";

const clock = new THREE.Clock();

let canvas, renderer, mixer;

const scenes = [];

init();
animate();

function init() {

	canvas = document.getElementById('c');

	const content = document.getElementById('items');
	const text = document.getElementById('text');
	var counter = 0;

	doors.forEach(function (door) {

		const scene = new THREE.Scene();

		// make a list item
		const element = document.createElement('div');
		element.className = 'list-item';

		const sceneElement = document.createElement('div');
		element.appendChild(sceneElement);

		const descriptionElement = document.createElement('div');
		descriptionElement.innerText = 'TürID: ' + door + "\nRaum: " + tueren[counter].split(" ")[2];
		element.appendChild(descriptionElement);

		var buttonform = document.createElement("form");
		var link = "";
		if (keycardid != null) {
			link = "testzentrum/" + door + "/" + keycardid;
		}
		else {
			link = "testzentrum/" + door + "/0";
		}

		buttonform.setAttribute("action", link);

		const openButton = document.createElement('button');
		openButton.innerText = 'Öffnen';
		openButton.setAttribute("type", "submit");

		buttonform.appendChild(openButton);
		element.appendChild(buttonform);

		// the element that represents the area we want to render the scene
		scene.userData.element = sceneElement;
		content.appendChild(element);

		//camera
		const camera = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 0.01, 1000);
		camera.position.x = 0;
		camera.position.y = 0;
		camera.position.z = 5;

		camera.rotation.x = 0;
		camera.rotation.y = 0;
		camera.rotation.z = 0;

		scene.userData.camera = camera;

		//controls
		const controls = new OrbitControls(scene.userData.camera, scene.userData.element);
		controls.minDistance = 2;
		controls.maxDistance = 5;
		controls.enablePan = false;
		controls.enableZoom = false;
		controls.minPolarAngle = Math.PI / 2;
		controls.maxPolarAngle = Math.PI / 2;
		controls.mouseButtons = {
			LEFT: THREE.MOUSE.ROTATE,
			RIGHT: THREE.MOUSE.ROTATE
		};
		scene.userData.controls = controls;

		if (access.includes(door)) {

			//greendoor
			const greenLoader = new GLTFLoader();
			const greengroup = new THREE.Group();
			greenLoader.load(doormodelgreen, function (gltf) {
				const model = gltf.scene;
				greengroup.add(model);
				scene.add(model);

				//animation
				mixer = new THREE.AnimationMixer(model);
				const clips = gltf.animations;

				clips.forEach(function (clip) {
					const action = mixer.clipAction(clip);
					//action.play();
				});

			}, undefined, function (error) {
				console.error(error);
			});

			if (keycardid == null) {
				openButton.addEventListener("click", function () {
					alert(`Sie müssen eine Karte auswählen`);
				}, false);
			}
			else if (status == 'Deaktiviert') {
				openButton.addEventListener("click", function () {
					alert(`Karte ist deaktiviert`);
				}, false);
			}
			else if (status == 'Gesperrt') {
				openButton.addEventListener("click", function () {
					alert(`Karte ist gesperrt`);
				}, false);
			}
			else if (startday > today) {
				openButton.addEventListener("click", function () {
					alert(`Karte hat das Startdatum noch nicht erreicht`);
				}, false);
			}
			else if (today < endday) {
				openButton.addEventListener("click", function () {
					alert(`Karte ist abgelaufen`);
				}, false);
			}
			else {
				openButton.addEventListener("click", function () {
					alert(`Tür ${door} geöffnet`);
				}, false);
			}
		}
		else if (!access.includes(door)) {
			//reddoor
			const redLoader = new GLTFLoader();
			const redgroup = new THREE.Group();
			redLoader.load(doormodelred, function (gltf) {
				const model = gltf.scene;
				redgroup.add(model);
				scene.add(model);

				//animation
				mixer = new THREE.AnimationMixer(model);
				const clips = gltf.animations;

				clips.forEach(function (clip) {
					const action = mixer.clipAction(clip);
					//action.play();
				});

			}, undefined, function (error) {
				console.error(error);
			});

			if (keycardid == null) {
				openButton.addEventListener("click", function () {
					alert(`Sie müssen eine Karte auswählen`);
				}, false);
			}
			else if (status == 'Deaktiviert') {
				openButton.addEventListener("click", function () {
					alert(`Karte ist deaktiviert`);
				}, false);
			}
			else if (status == 'Gesperrt') {
				openButton.addEventListener("click", function () {
					alert(`Karte ist gesperrt`);
				}, false);
			}
			else if (startday > today) {
				openButton.addEventListener("click", function () {
					alert(`Karte hat das Startdatum noch nicht erreicht`);
				}, false);
			}
			else if (today < endday) {
				openButton.addEventListener("click", function () {
					alert(`Karte ist abgelaufen`);
				}, false);
			}
			else {
				openButton.addEventListener("click", function () {
					alert(`Sie haben keine Berechtigung für Tür ${door}`);
				}, false);
			}

		}

		//lights
		scene.add(new THREE.HemisphereLight(0xaaaaaa, 0x444444));
		//const light = new THREE.DirectionalLight( 0x555555, 0.5 );
		//light.position.set( 1, 1, 1 );
		//scene.add( light );

		scenes.push(scene);
		counter++;
	})

	renderer = new THREE.WebGLRenderer({ canvas: canvas, antialias: true, alpha: true });
	renderer.setClearColor(0x000000, 0);
	renderer.setPixelRatio(window.devicePixelRatio);

}

function updateSize() {

	const width = canvas.clientWidth;
	const height = canvas.clientHeight;

	if (canvas.width !== width || canvas.height !== height) {

		renderer.setSize(width, height, false);

	}

}

function animate() {

	render();
	requestAnimationFrame(animate);

}

function render() {

	updateSize();

	//canvas.style.transform = `translateY(${ window.scrollY }px)`;

	renderer.setClearColor(0x000000, 0);
	renderer.setScissorTest(false);
	renderer.clear();

	renderer.setClearColor(0x000000, 0);
	renderer.setScissorTest(true);

	scenes.forEach(function (scene) {

		// so something moves
		//scene.children[ 0 ].rotation.y = Date.now() * 0.001;

		// get the element that is a place holder for where we want to
		// draw the scene
		const element = scene.userData.element;

		// get its position relative to the page's viewport
		const rect = element.getBoundingClientRect();

		// check if it's offscreen. If so skip it
		if (rect.bottom < 0 || rect.top > renderer.domElement.clientHeight ||
			rect.right < 0 || rect.left > renderer.domElement.clientWidth) {

			return; // it's off screen

		}

		// set the viewport
		const width = rect.right - rect.left;
		const height = rect.bottom - rect.top;
		const left = rect.left;
		const bottom = renderer.domElement.clientHeight - rect.bottom;

		renderer.setViewport(left, bottom, width, height);
		renderer.setScissor(left, bottom, width, height);

		const camera = scene.userData.camera;

		camera.aspect = width / height;
		camera.updateProjectionMatrix();

		scene.userData.controls.update();

		if (mixer) {
			mixer.update(clock.getDelta());
		}

		renderer.render(scene, camera);

	});

}

/*
window.onscroll = function (ev) {
  if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
	canvas.style.transform = `translateY(0px)`;
  }
};
*/
